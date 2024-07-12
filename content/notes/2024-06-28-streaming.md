+++
title = "Streaming de données"
date = "2024-06-28"
description = "Ce qu'est le streaming, comment l'utiliser et quel est sa pertinence"
[taxonomies]
tags = ["basiques"]
+++

Fluidifier la donnée est un moyen d'éviter des problématiques de mémoire lorsqu'on sait qu'elle est volumineuse ainsi qu'un moyen de réactivité lorsqu'on sait que la donnée que l'on souhaite recevoir dépend de comportements non synchrones.
C'est via la notion de streams que les développeurs traitent ces enjeux et cette note regroupe mes pensées sur ce sujet mal compris et des bases pour mieux le comprendre.

# Un sujet mal compris
Le streaming est un sujet complexe et souvent mal compris par les développeurs pour plusieurs raisons.
Des malentendus peuvent provenir de la nature technique du streaming, des attentes des utilisateurs, des contraintes économiques et des défis de mise en œuvre.
 * *Habitudes de Développement et Conceptions Erronées*: beaucoup de développeurs sont habitués à des cycles de gestion de la données bien différents (ex: requête/réponse HTTP, envoi de fichiers), ce qui les retient de concevoir des systèmes capables de gérer des flux de données continus. On voit aussi, de ces habitudes, sortir des anti-patterns comme lorsque les websockets sont utilisées pour imiter des envois requêtes/réponses traditionnels.
 * *Complexité inhérente*: lié aux sujets de streaming arrivent des enjeux de consistance, de débit ou d'ordre des messages qui sont nouvelles et supplémentaires à la complexité des problèmes traités par les développeurs (fonctionnelle ou technique).

# Un mode d'échange de données à part
Regarder la donnée comme un flux d'éléments plutôt que dans son entièreté est très utile puisqu'il se rapproche de la manière dont les ordinateurs eux-même envoient et reçoivent de la donnée (via TCP par exemple).
Il s'agit aussi parfois d'une nécessité lorsque les jeux de données manipulés sont trop larges pour être traités d'une traite.
A très haut niveau, le fonctionnement d'un stream consiste à ne charger qu'une partie des données en mémoire et ne charger une autre partie que lorsque le traitement souhaité à été effectué.
Puisqu'un des enjeux des streams est de permettre cette gestion fluidifiée en mémoire, il est nécessaire d'avoir une gestion intelligente de la consommation de sa source pour ne justement pas surcharcher cette mémoire.
La fonctionnalité liée à cet enjeu s'appelle la *backpressure* et est au coeur de l'initiative [Reactive Stream](https://www.reactive-streams.org/): il s'agit d'être en mesure de ralentir la production si la consommation n'est pas en mesure de suivre.

De ce sujet de traitement fluidifié et de backpressure découle les complexités perçues par les développeurs.
Puisque la donnée arrive au fil de l'eau et est traitée à leur rythme par les consommateurs, il arrive des cas où les différents consommateurs d'une même donnée sont à une étape différente du traitement du stream avec certains plus avancés que d'autres.
Leur état est alors dit *éventuellement consistant*, au sens où on sait qu'il existe un moment dans le temps où ils auront tous les deux traité le même événement et seront donc consistants sur l'état lié à cet événement.

Même si ces complexités additionnelles sont inhérentes aux systèmes que l'on traite, ils sont le coût de cette nouvelle corde à votre arc qu'il convient de bien considérer.

Quelques articles liés que j'ai apprécié:
* [Never* use Datagrams](https://quic.video/blog/never-use-datagrams/) by [@kixelated](https://github.com/kixelated)
