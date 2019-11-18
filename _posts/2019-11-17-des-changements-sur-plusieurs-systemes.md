---
layout: post
title: Des changements sur plusieurs systèmes
date: 2019-11-17
categories: STM,ZIO,architecture,distributed programming,scala
comments: true
---

Lors de mon dernier projet, j'ai eu à effectuer la difficile tâche de stocker une même donnée dans deux bases de données différentes. C'est une chose qui peut arriver souvent, par exemple dans le cas où j'ai une base primaire qui me sert de source de vérité et une seconde base de donnée qui est optimisée pour la lecture et qui permet aux utilisateurs de mon application d'accéder très rapidement à ma donnée.

## Pourquoi cette tâche est difficile ?

Cette tâche comporte quelques difficultés car je travaille dans le web, et que mon système est distribué. Plusieurs choses peuvent donc mal se passer dans ce scénario simple.

Premièrement, il est possible qu'une fois que j'ai fait la mise à jour dans ma base primaire, je n'arrive pas à accéder à la seconde, ou que la seconde transaction ne puisse pas s'appliquer. En fonction du scénario qui m'intéresse, je peux ici envisager plusieurs solutions:

1. **Réessayer**: quitte à perdre un peu de temps, je réessaye d'appliquer les modifications sur ma seconde base. Il est possible que j'ai à ressayer plus d'une fois. Pendant ce temps, ma première base conserve sa modification: mes deux systèmes sont alors désynchronisés pendant un moment.
2. **Annuler** le premier changement: sans perdre plus d'une seconde, ou après avoir essuyé quelques échecs à relancer ma modification, je décide d'annuler mon premier changement. Cela peut consister en un rollback, ou bien alors en un commit de la transaction inverse. Mes deux bases sont alors de nouveau en synchronisation.

Ce problème est lié à **l'atomicité** d'une modification: si l'opération est décrite comme étant en succès, alors tous les changements ont correctement été appliqués; si l'opération a échouée, alors les changements qui ont été appliqués sont dés-appliqués si bien que le résultat est le même que si rien ne s'était passé.

Deuxièmement, il est possible qu'une deuxième personne ait souhaité le même document que moi. S'il est possible que les différentes actions aient lieu dans l'ordre (M1 → M2 → N1 → N2), il est aussi possible que l'ordre soit un peu bouleversé (M1 → N1 → N2 → M2) menant à appliquer le changement N dans ma base principale mais le changement M dans ma seconde base. Mes deux bases sont encore désynchronisées dans le sens où même si cette fois tous les changements y ont bien été appliqués, les données qui y résident sont dans des états différents.

Ce problème est lié à **l'isolation** de notre modification: cette propriété garantie qu'une seule modification est appliquée à la fois.

## Résoudre ces problèmes avec une architecture appropriée

Une première façon de résoudre ces problèmes peut être d'accepter la dé-synchronisation temporaire entre nos deux bases pendant un court moment pour s'assurer qu'elles seront éventuellement consistantes dans le temps. Il s'agit de construire notre application de manière à y inclure le mécanisme permettant de réessayer dont nous parlions plus haut.

### Un système événementiel

Il existe plusieurs manières de faire cela. Il est possible par exemple de tourner l'application vers un système événementiel: on sauvegarde dans une unique base les différents événements qui ont lieu sur l'application et différents services écouteront ces changements pour y réagir. Ce système d'architecture est connu sous le nom d'event sourcing.

Il est important que la solution que l'on choisisse assure une gestion de l'ordre des changements à appliquer dans notre système. Des systèmes comme Kafka assurent que l'on peut y stocker nos événements dans l'ordre et qu'ils seront aussi lus dans l'ordre, ou au moins au sein de leur partition, c'est à dire pour une même ressource. Cela nous permet de dépasser le problème d'isolation puisque l'on peut s'assurer qu'une seule modification, pour une ressource donnée, est appliqué à la fois.

### Regardons ce qu'on a créé

On se retrouve avec une sorte de machine de guerre qui ressemble à cela:

1. Base stockant les événements en append only (garantie d'ordre et garantie d'atomicité sur cette seule base)
2. Système dépilant les derniers événements et les publiant dans un système de queue (Kafka ou similaire)
3. Services abonnés à la queue pour réagir aux changements et les appliquer sur un autre système (base de donnée secondaire, envoi d'email, ...)

S'il n'y a qu'un seul système supplémentaire à mettre à jour, comme dans mon cas initial, une base de donnée optimisée pour la lecture, on peut faire l'économie d'une file de message et faire en sorte que le système qui dépile les derniers événements applique directement les changements plutôt que de publier sur Kafka.

### Le piège

Même si on pense avoir résolu le problème ici, on est en réalité plutôt en train de le cacher autre part: dans l'étape 2, le système de dépilage doit se souvenir du dernier événements dépilé pour ne pas le reprendre une seconde fois. On est alors de nouveau en train d'appliquer 2 effets en même temps: sauvegarder le dernier événement dépilé et le publier dans une queue. Il est alors nécessaire d'appliquer une dernière propriété au reste de notre système: l'**idempotence**. Un gros mot qui signifie qu'une modification appliquée plusieurs fois de suite sans changer l'effet de la première application. Avec cette propriété, le système 2 peut se tromper et envoyer plusieurs fois le même message dans Kafka.

## Résoudre ces problèmes juste avec du code

Si on souhaite résoudre ces problèmes juste avec du code, il est tout d'abord nécessaire d'implémenter les mécanismes de ré-essai et d'annulation pour chacun des appels à des services externes qui ont lieu. Il est possible d'imaginer une brique logicielle qui se charge d'appliquer les changements et de les réessayer ou de se charger de l'annulation si le changement n'est pas possible comme nous l'avions décrit au début de cet article. Ce genre de systèmes s'appelle Mémoire Transactionnelle Logicielle (qu'on retrouve plus facilement en anglais, [STM](https://en.wikipedia.org/wiki/Software_transactional_memory)). Des implémentations élégantes existes, et le monde Scala entend notamment parlé de l'implémentation [ZIO](https://www.youtube.com/watch?list=PL8NC5lCgGs6MYG0hR_ZOhQLvtoyThURka&v=d6WWmia0BPM).

Il est nécessaire de s'assurer que notre STM gère aussi le problème d'isolation. La documentation de ZIO définit le type STM comme suit:

> STM[E,A] represents an effect that can be performed transactionally, resulting in a failure E or a value A

Ici, "transactionally" signifie qu'il y a isolation des autres transactions à la lecture comme à la lecture des entités transactionnelles. Ce problème est résolu en traquant toutes les lectures et les écritures, de manière à ce qu'à la fin d'une transaction, si une valeur transactionnelle impliquée a été changée par une autre transaction, elle est réessayée.

Une limitation de ce genre de ce système réside alors dans le fait que tout effet n'est pas forcément annulable ou ré-essayable, comme c'est par exemple le cas avec un envoi d'email ou l'utilisation d'un service de virement (on peut donner de l'argent à quelqu'un, mais pas lui en prendre sans son autorisation). En somme, ce genre de structure permet de décrire les effets que l'application doit produire mais n'est pas responsable de leur application. Il s'agit d'une approche très puissante pour résoudre beaucoup de problèmes, mais pas forcément tous.

Cet article a présenté beaucoup de concepts sans forcément tous les creuser. Il permet, je l'espère de comprendre des problématiques que l'on retrouve souvent dans des applications web. J'ai donc disposé quelques liens pour qu'ils puissent être creusés si nécessaire.

**Sources:**

- [Keeping Elasticsearch in Sync](https://www.elastic.co/fr/blog/found-keeping-elasticsearch-in-sync)
- [STM](https://en.wikipedia.org/wiki/Software_transactional_memory)
- [ZIO and Cats effects](http://degoes.net/articles/zio-cats-effect)
- Video [ATOMICALLY { DELETE YOUR ACTORS }](https://www.youtube.com/watch?list=PL8NC5lCgGs6MYG0hR_ZOhQLvtoyThURka&v=d6WWmia0BPM)
- [STM systems: Enforcing strong isolation between transactions and non-transactional code](https://hal.inria.fr/hal-00699903/document)
