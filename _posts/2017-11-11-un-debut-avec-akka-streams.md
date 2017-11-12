---
layout: post
title:  "Débuter avec Akka streams"
date:   2017-11-11
categories: akka streams, streams
comments: true
---

J'ai commencé à utiliser Akka Streams sur mon dernier projet. Voici les patterns que j'ai utilisé et les éléments de compréhension qui me semblent utiles.


## Les streams

Pour commencer, Akka Streams est une librairie qui permet de manipuler des flux de données (streams). Les streams sont de plus en plus utilisés aujourd'hui puisque les quantités de données que l'on manipule deviennent trop importantes pour être embarquées entièrement sur une machine, mais aussi parce qu'ils représentent très bien la façon dont la donnée arrive effectivement sur nos machines, par exemple lorsqu'on les reçoit par TCP.

Manipuler des streams revient à composer des opérations simples sur la donnée, petits bouts par petits bouts, souvent au moment où ils nous arrivent, plutôt que d'attendre d'avoir tout reçu pour effectuer des opérations qui seront coûteuses puisque sur des structures imposantes.

La librairie est construite au dessus du modèle des acteurs, cher à Akka, et peut donc être vue comme une abstraction sur les problématiques de réception/transmission de messages, de backpressure (les messages arrivent trop vite pour être traités) et de typage. Elle respecte les standards [Reactive Streams](http://www.reactive-streams.org/) relatifs aux traitement de streams asynchrones avec une backpressure non bloquante; cela lui permet de se brancher à d'autres librairies adhérant à ce standard comme [RxJava](https://github.com/ReactiveX/RxJavaReactiveStreams) ou [Slick](http://slick.lightbend.com/).

## Les concepts de base

Pour manipuler les streams avec Akka streams, on se retrouve à manipuler essentiellement 3 types: les `Source`s, les `Sink`s et les `Flow`s.

### Sources

A Source is a data creator, it serves as an input source to the stream. Each Source has a single output channel and no input channel. All the data flows through the output channel to whatever is connected to the Source.

Une [`Source`](https://doc.akka.io/api/akka/current/akka/stream/scaladsl/Source.html) est une étape du traitement d'où provient la donnée, elle sert de point d'entrée au stream. Ainsi, chaque `Source` possède exactement un canal de sortie et pas de canal d'entrée. Toute la donnée transite depuis ce canal de sortie jusqu'à ce qui est connecté à cette source. Akka Streams assure qu’elle n’emettra un nouvel élément que lorsque les étapes de traitement suivantes sont prêtes à le recevoir.

Elles sont définies de cette façon:

{% highlight scala %}
val source: Source[Int, NotUsed] = Source(1 to 10)
{% endhighlight %}

Ce bout de code créé donc une source qui générera des entiers allant de 1 à 10. Le type d'une source est constitué de deux autres types: celui de l'élément qui en sortira, ici `Int`, ainsi que celui qui sera matérialisé lorsque la `Source`sera consommée (dans cet exemple, il nous importe peu et `NotUsed` explicite bien cela).

### Sinks

Un [`Sink`](https://doc.akka.io/api/akka/current/akka/stream/scaladsl/Sink.html) est globallement l'opposé d'une source. Il s'agit d'une étape du traitement qui sonne la fin du stream puisque son but est de consommer la donnée. Il possède exactement un canal d'entrée et pas de canal de sortie. Ils sont définis de cete façon:

{% highlight scala %}
val sink: Sink[Int, Future[Done]] = Sink.foreach(println)
{% endhighlight %}

Nous venons de créer un consommateur qui affichera (`println`) tous les éléments qu'il acceptera. Le type d'un `Sink` est constitué, comme pour la `Source`, de deux autres types: celui de l'élément consommé (`Int` ici) ainsi que le type résultant lorsque le `Sink` aura consommé la donnée.

Ici, le type de retour du `Sink` peut nous intéresser plus que celui de la `Source`. En effet, puisqu'il s'agit de l'étape de fin du stream, on peut vouloir déclencher des opérations particulières lorsqu'un `Sink` se termine. Un `Future` permet de très bien subvenir à ce besoin (`Done` est identique à `Unit` et ne porte pas d'autre information que celui d'un signal de complétion).

### Flows

Un [`Flow`](https://doc.akka.io/api/akka/current/akka/stream/scaladsl/Flow.html) est une étape du traitement avec exactement un canal d'entrée et un calan de sortie. Il permet de connecter les `Source`s aux `Sink`s en transformant les éléments qui y passent.

{% highlight scala %}
val flow: Flow[Int, Int, _] = Flow[Int].map(_ * 2)
{% endhighlight %}

Tous les éléments qui rentreront dans ce flow seront mutipliés par deux en sortant. Le type d'un `Flow` est constitué de trois autres types: celui des éléments en entrée (`Int`) suivi de celui des éléments en sortie (`Int`) et le dernier qui sera matérialisé lorsque la donnée qui y sera passé sera consommée.

Connecter un `Flow` à une `Source` donne une `Source` d'un nouveau type, et que le connecter à un `Sink` donne un `Sink` d'un nouveau type aussi:

{% highlight scala %}
val source: Source[Int, _] = Source(1 to 10)
val flow: Flow[Int, Boolean, _] = Flow[Int].map(_ % 2 == 0)
val sink: Sink[Boolean, _] = Sink.fold(true)(_ && _)

val sourceAndFlow: Source[Boolean, _] = source.via(flow)
val flowToSink: Sink[Int, _] = flow.to(sink)
{% endhighlight %}


### Synthèse

Ces trois types peuvent être connectés de manière à représenter un graphe définissant le traitement de la donnée:

{% highlight scala %}
val graph: RunnableGraph[NotUsed] = 
  source
    .via(flow)
    .to(sink)
{% endhighlight %}

Les éléments de la `Source` sont transformés en passant par le `flow` avant d'être consommés par le `Sink`. Avec ce bout de code, nous obtenons une définition d'un graphe qui n'attend plus que d'être exécuté. 

Pour cela, il existe la fonction `run()`. Elle permet d'interpréter le graphe et de matérialiser une valeur de retour. Il s'agit du type qui apparaît en dernier dans les signatures plus haut.

**Tant que cette méthode n'est pas appelée, aucune donnée ne sera générée par la `Source` ou consommée par le `Sink`.**

Notons que si la création d'un flow est parfois lourde, il est possible d'utiliser des opérateurs tels que `map`, `filter` ou `collect` sur une source pour simplifier l'écriture:

{% highlight scala %}
val graph: RunnableGraph[NotUsed] = 
  source
    .map(_ % 2 == 0)
    .to(sink)
{% endhighlight %}


## Les utilisations

L'utilisation d'Akka Streams nécessite rarement de connaître plus de types. La librairie possède quelques connecteurs en son coeur permettant de créer des `Source`s, `Flow`s ou `Sink`s pour des cas courant comme la lecture de fichiers, la consommation de `Future`s. Le projet [Alpakka](https://github.com/akka/alpakka) présente de plus de nombreux connecteurs pour des technologies/protocoles tiers, comme par exemple à ElasticSearch, Cassandra, FTP et bien d'autres.

### Lecture d'un fichier

On trouve par exemple dans le coeur de la librairie une méthode permettant de créer une `Source` pour un fichier à partir d'un `InputStream`.

{% highlight scala %}
// StreamConverters.scala

def fromInputStream(
  in: () ⇒ InputStream, 
  chunkSize: Int = 8192
): Source[ByteString, Future[IOResult]]
{% endhighlight %}

Cette méthode permet de lire un fichier par parties, avec des éléments avec une taille de `chunkSize`. A partir de cette source, nous pouvons connecter des `Flow`s et `Sink`s nécessaires à nos étapes de traitement.

{% highlight scala %}
StreamConverters.fromInputStream(() => inputStream)
  .via(lineParser)
  .to(Sink.foreach(println))
{% endhighlight %}

Dans ce cas, les lignes du fichier sont parsées avant d'être affichées en console. Les trois concepts simples que nous avons vus nous permettent donc d'exécuter nos flows métiers de manière élégante et plutôt simplement. Grâce aux connecteurs que nous proposent les différentes librairies autour du projet Akka Streams, il est très simple de s'interfacer avec différents systèmes:

{% highlight scala %}
Sftp
  .fromPath("/myExampleFile.tsv", sftpSettings)
  .via(parsingFlow)
  .map { tsvLine => 
    new ProducerRecord[String, TsvLine]("topic1", tsvLine)
  }
  .to(Producer.plainSink(producerSettings))
{% endhighlight %}

Avec les connecteurs pour [Kafka](https://doc.akka.io/docs/akka-stream-kafka/current/home.html) et [FTP](https://github.com/akka/alpakka/tree/master/ftp/src/main/scala/akka/stream/alpakka/ftp), je peux en quelques lignes consommer les éléments provenant d'un topic fichier et envoyer dans un topic Kafka.


### Des graphes plus complexes

Nous avons vu que les `Source`s sont des éléments qui n'ont qu'un seul output et que les `Sink`s n'ont qu'un seul input. Cette définition ne nous permet que des utilisations de streams très linéaires où une source est toujours branchée à un seul et unique Sink.

La librairie propose différentes méthodes permettant d'attacher plusieurs `Sink`s à une seule source. La plus simple est d'utiliser la fonction `alsoTo`. Elle permet d'attacher un `Sink` à un flow donné, ce qui signifie que tout élément qui passera par ce `Flow` sera aussi envoyée dans ce `Sink`. Dans notre cas précédent, on peut imaginer que l'étape de parsing peut retourner des lignes valides (`Valid`) ou invalides (`Invalid`). Il peut alors être intéressant d'envoyer les erreurs qui apparaissent dans un topic Kafka différent.

{% highlight scala %}
Sftp.fromPath("/myExampleFile.tsv", sftpSettings)
  .via(parsingFlow)
  .alsoTo(parsingErrorLoggerSink)
  .collect { case Valid(record) => 
    new ProducerRecord[String, TsvLine]("topic1", tsvLine)
  }
  .to(Producer.plainSink(producerSettings))
{% endhighlight %}


Pour des cas plus complexes, il peut être nécessaire d'utiliser le DSL de graphe porposé par Akka Streams. Il adopte une notation assez graphique qui permet d'expliciter assez clairement le comportement de notre graphe de traitement. On y retrouve [plusieurs styles de jonctions](https://doc.akka.io/docs/akka/2.5/scala/stream/stream-graphs.html#constructing-graphs) entre les différentes parties du graphe qu'on peut répartir en deux catégories:
 
 * Fan-in qui prennent plusieurs inputs et ne ressortent qu'un seul output
 * Fan-out qui ne prennent qu'un seul input et ressortent plusieurs outputs

Un objectif que nous pourrions nous fixer est le suivant:

![Stream graph](/assets/img/2017-11-11-un-debut-avec-akka-streams/akka-drawing.png){:class="img-responsive"}

Partant d'un graphe d'entrée dont chaque élément est un tuple, nous divisons chaque élément et appliquons une fonction sur le second élément du tuple uniquement avant de rassembler de nouveau les deux éléments.

{% highlight scala %}
// La méthode `create` nous donne un builder `b`

Flow.fromGraph(GraphDSL.create() { implicit b =>
  // Nous ajoutons les deux opérations de jonction au builder

  // pour pouvoir les utiliser sur notre stream ensuite.

  val unzip = b.add(Unzip[FileMeta, KafkaMessage]) // Fan-out
  
  val zip = b.add(Zip[FileMeta, KafkaResult]) // Fan-in

  // Nous dessinons notre graphe ici.

  unzip.out0                   ~> zip.in0
  unzip.out1 ~> kafkaProducer  ~> zip.in1

  FlowShape(unzip.in, zip.out)
})
{% endhighlight %}

Appliquer une fonction, dans le monde des streams, revient à faire passer nos valeurs par un flow. Dans cet exemple, nous publions sur Kafka la seconde valeur de notre tuple. Nous récupérons donc un `KafkaResult` de notre flow.


## Conclusion

Il y a bien évidemment beaucoup d'autres choses à dire sur Akka Streams mais je vais m'arrêter là pour ce post; leur documentation couvre des sujets comme la gestion des erreurs ou la manière dont les flows peuvent être testés.

Pour résumer, la librairie offre des outils simples pour traiter des quantités de données importantes de manière fonctionnelle, mais permet aussi de descendre un peu plus bas niveau pour des schémas de traitement plus complexes. Les intégrations avec d'autres outils se font simplement avec les nombreux connecteurs disponibles
