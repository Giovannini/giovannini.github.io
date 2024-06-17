+++
title = "Les transformeurs de monades"
date = "2016-10-09"
description = "Les transformeurs de monades (monad transformers en anglais) sont des objets provenant de la programmation fonctionnelle."
[taxonomies]
tags = ["scala", "monade"]
+++

Les transformeurs de monades (monad transformers en anglais) sont des objets provenant de la programmation fonctionnelle.
Il s'agit de types monadiques dont le but est d'abstraire une monade enveloppant une seconde monade.
Nous allons voir qu'il s'agit d'un motif que nous retrouvons assez souvent dans notre code Scala et en quoi les transformeurs de monades sont une abstraction très pratique pour ce genre de cas.


## Les cas d'utilisation

Je viens de créer une superbe base de données qui va me permettre de gérer ma todolist d'une manière révolutionnaire.
J'ai plusieurs méthodes sur cette base, notamment `def get(id: Long): Future[Option[Task]]` qui me permet de récupérer une tâche de ma todolist via son ID.
Ici, j'utilise le type `Future` parce que je souhaite effectuer des transactions asynchrones avec ma base.
De plus, j'utilise le type `Option` puisqu'en réalité, je ne sais pas si à l'ID que je donne en paramètre correspond vraiment une tâche.
Top.
Donc maintenant, je peux sereinement récupérer mes tâches une par une.
```scala
def getTaskInfo(id: Long) = {
  // Récupération de ma tâche et opération sur le contenu du Future
  todolistDAO.get(id).map {
    case None => println("Pas de tâche")
    case Some(task) => println(task)
  }
}
```

Mais ma todolist est révolutionnaire et elle peut faire bien plus que ça.
En vrai, j'ai aussi une autre méthode `def sumTwoTasksDurations(id1: Long, id2: Long)`.
Je vais devoir faire deux appels à ma méthode `get`, j'aurais deux `Future`s.
Comme ils ont une structure monadique, je vais pouvoir utiliser une for comprehension:
```scala
def sumTwoTasksDurations(id1: Long, id2: Long) = for {
  maybeTask1 <- todolistDAO.get(id1)
  maybeTask2 <- todolistDAO.get(id2)
} yield {
  ...
}
```
Dans mon `yield`, je vais devoir traiter deux `Option`s:

 * Si elles sont toutes les deux à `Some`, je les affiche
 * Sinon, je n'affiche rien


Comme `Option` possède aussi une structure monadique, je peux aussi utiliser une for comprehension:
```scala
def sumTwoTasksDurations(id1: Long, id2: Long) = for {
  maybeTask1 <- todolistDAO.get(id1)
  maybeTask2 <- todolistDAO.get(id2)
} yield {
  for {
    task1 <- maybeTask1
    task2 <- maybeTask2
  } yield task1.duration + task2.duration
}
```

Ce qu'on voit ici est qu'il est impossible d'accéder à l'objet qui nous intéresse, `task`, sans passer par les deux monades `Future` et `Option`.
Ca n'a pas l'air trop gênant lorsqu'on manipule une seule instance de ce type, mais dès qu'on en a plusieurs, notre code grossit.

Les transformeurs de monade ont justement pour but de pouvoir nous faire accéder à l'objet qui nous intéresse en une seule fois.

## L'entrée en jeu des transformeurs de monades

(J'utiliserai la librairie [Scalaz](https://github.com/scalaz/scalaz){:target="_blank"} pour les exemples suivant, elle n'est pas la seule à proposer des transformeurs de monades.)

Les transformeurs de monades sont choisis en fonction de la structure monadique intérieure.
Dans notre exemple, `Future[Option[Task]]`, il s'agit d'`Option[Task]`.
Nous allons donc choisir le transformeur `scalaz.OptionT`, avec T pour Transformer, qui a une fonction faite pour nous:
```scala
def apply[A](a: M[Option[A]]) = new OptionT[M, A](a)
```
On va donc pouvoir appliquer cette fonction `apply` à notre double structure monadique pour la transformer en une structure simple.


D'où ensuite ce code bien plus simple:
```scala
def sumTwoTasksDurations(id1: Long, id2: Long): OptionT[Future, Int] = for {
  task1 <- OptionT(todolistDAO.get(id1))
  task2 <- OptionT(todolistDAO.get(id2))
} yield task1.duration + task2.duration
```

Le type de retour est aussi un `OptionT` ici, et je vais pouvoir le retransformer en les types que je sais maîtriser via la méthode `run`:
```scala
val durationT: OptionT[Future, Int] = displayTwoTasksInfo(1, 2)
val futureMaybeDuration: Future[Option[Int]] = durationT.run
```

## Conclusion

Les transformeurs de monades proposent une abstraction puissante pour travailler sur objets contenus dans des monades elles même contenues dans une première monade.
Ils permettent de réduire la complexité du code et améliorent la lisibilité du code en faisant tout le branchement nécessaire pour récupérer la donnée intéressante.
SCalaz est une des librairies proposant des transformeurs de monades, mais il en existe d'autre, et il est aussi possible de créer les siens.

* [La librairie Scalaz](https://github.com/scalaz/scalaz)
* [Stacking Future and Either by eed3si9n](http://eed3si9n.com/herding-cats/stacking-future-and-either.html)
