---
layout: post
title:  "Une architecture event sourcée et CQRS"
date:   2017-01-07
categories: event sourcing, cqrs, architecture
comments: true
---

Ce post est une tentative d'explication des principes d'une architecture d'application se basant sur l'Event Sourcing et le pattern CQRS.


## Une définition rapide

L'Event Sourcing est un pattern d'architecture d'application qui consiste à persister non pas l'état courant de l'application, mais plutôt tous les changements (ou événements) qui y ont eu lieu et qui ont impliqué un changement de cet état.

De son côté, CQRS est aussi un pattern dont l'acronyme signifie Command and Query Responsibility Segregation, c'est à dire pour les anglophobes la séparation des responsabilités d'écriture et de lecture.
Cela signifie séparer dans son applications les parties qui seront dédiées à l'écriture et à la lecture en base.

Nous allons rentrer dans les détails de ces patterns et voir qu'ils jouent très bien ensemble.


## L'event sourcing

Comme je l'ai dit précédemment, une architecture event sourcée persiste les événements qui ont impliqué un changement de l'état de l'application.
Essayons de prendre un cas concret pour voir comment ce principe s'y applique.

### L'exemple du Pokédex

Le Pokédex est une mini encyclopédie qui recense les espèces de Pokémon que rencontre le joueur au cours de son aventure.
Quand on le reçoit, il est vide. C'est son état initial.

[comment]: <> (Include an empty pokédex image)

Le principe d'une architecture event sourcée consiste à enregistrer tous les événements qui ont lieu sur notre système dans des objets que l'on appelle événement et que l'on va enregistrer dans notre base de donnée plutôt que l'état courant de notre système.
Si je rencontre un Pokémon sauvage, je vais envoyer un événement à mon système, notifiant que j'ai rencontré un Pokémon.

[comment]: <> (Include an event that I append to my initial state)

Puisque j'ai enregistré un changement de l'état de mon application, je peux donc affirmer que je ne vais plus avoir le même état que précédemment.

[comment]: <> (Include an pokédex image with one pokémon)

S'il se trouve qu'en plus, j'ai capturé ce pokémon, je vais pouvoir avoir plus d'informations sur lui.
Il s'agit d'un nouvel événement que je vais aussi enregistrer à la suite.

[comment]: <> (Include an event that I append to the event list)

Une fois de plus, j'ai enregistré un nouvel événement, je peux inférer que l'état de mon système a évolué.

[comment]: <> (Include an pokédex image with one pokémon which has been captured)


Nous venons de voir comment fonctionne une architecture event sourcée: j'enregistre uniquement les changements que j'ai effectué sur mon système et c'est grâce à cela que j'arrive à savoir que l'état de mon système a évolué.
Ma base de donnée consiste alors en une longue liste de tous les événements qui ont eu lieu sur mon système, on l'appelle généralement event log.


### Les propriétés d'une architecture event sourcée

Nous venons de voir dans un exemple relativement simple la façon dont fonctionne une architecture event sourcée.
Il s'agit donc d'enregistrer tous les changements ayant lieu sur notre application sous forme d'événements.
Ces événements constituent l'historique de notre application et sont immuables, ils sont stockés dans une structure qu'on appelle souvent <b>Event Log</b>.
Cela implique une façon de penser un peu différente de ce qu'on peut voir dans d'autres architectures, ainsi que quelques propriétés que nous allons voir.

#### L'ordre des événements

Tout d'abord, l'event log est une structure qui est nécessairement triée en fonction de l'ordre dans lesquels les événements se sont produits.
Une des forces de l'event log est qu'il peut être appliqué à une seconde application avec un état initial similaire à la première pour en arriver au même état et avoir ainsi deux applications à l'état identique.

[comment]: <> (Include an event log when I see a Pokémon then I capture it)

Ainsi, en fonction de leur ordre, il serait possible d'arriver à des états différents ou même totalement incohérents.
Par exemple, si je lis mes événements un par un dans un mauvais ordre, disons:

[comment]: <> (Include an event log when I captured a pokémon before seeing it)

J'aurais si je décompose mes états, je trouverais à un moment:

[comment]: <> (Include an pokédex image with one pokémon which has been captured but not encountered)

Avoir capturé un pokémon sans savoir de quoi il s'agit, c'est un état incohérent.


L'ordre des événements dans l'event log est donc très important.
Cela nous assure que chaque application d'un nouvel événement modifie l'état de notre application en un nouvel état cohérent.

#### L'histoire de notre application

Une des choses que nous fait gagner notre event log, en conservant l'ordre d'occurrence des événements, est la possibilité de retracer toute l'histoire de notre application et de connaître son état à un moment donné en ne considérant qu'une partie des événements enregistrés. On peut appeler cela requête temporelle.

Il est aussi possible de supprimer l'état du système qu'on le connaît, puisqu'il suffit de rejouer les événements dans le bon ordre pour arriver au même état. L'event sourcing permet donc de reconstruire complètement l'état de son application juste en relançant les
événements sur une application "vide".

Enfin, si on se rend compte qu'un événement est historiquement faux, il est possible d'inverser son effet, ainsi que ceux de tous les événements qui l'ont suivi, jouer l'événement correct puis rejouer les événements qui le suivaient.

### Les avantages d'une telle architecture

Les événements sont des objets immutables, ils représentent l'histoire de notre application et peuvent donc être stockés via une opération append-only. Les éléments de l'application ayant généré les événements peuvent continuer à travailler sans attendre forcément un retour, et les composants qui se chargent de gérer les événements peuvent le faire en tâche de fond. Cela permet d'améliorer grandement la performance et la scalabilité d'une application, surtout au niveau de l'interface.

Ils sont des objets simples qui ont simplement pour but de décrire une action qui a eu lieu. Ils ne mettent donc pas directement à jour l'application
