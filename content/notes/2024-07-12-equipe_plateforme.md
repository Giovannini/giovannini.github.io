+++
title = "Equipe plateforme"
date = "2024-07-12"
description = "Une équipe plateforme est une équipe produit qui est concentrée sur sa mission technique avec des clients techniques et dont les livrables sont consommés par plusieurs verticales."
[taxonomies]
tags = ["plateforme", "équipe"]
+++

Une équipe plateforme est une **équipe produit** qui est concentrée sur sa mission technique avec des clients techniques et dont les livrables sont consommés par plusieurs verticales. Elle créé les fondations et des composants pour des produits favorisant la réutilisation et créent un domaine limité dans laquelle une expertise est centralisée.

# Quelques définitions

## Une équipe produit

Une équipe produit est multi-fonctionnelle et regroupe des acteurs du produit, du design et de l’ingénierie pour résoudre les problèmes auxquels elle fait face (tout comme une équipe de *delivery*). En revanche, elle se focalise sur les avantages qu’elle fournit à ses utilisateurs et donc sur la résolution des problèmes qu’on lui demande de régler. Ce qui l’importe est le résultat final (au sens de la valeur) et non pas le livrable lui-même.

Dans ce type d'équipe, il est important d’avoir et de cultiver une connaissance profonde des utilisateurs, de la donnée, de l'industrie et du métier. Avec cette connaissance, elle saura orienter les solutions qu’elle propose vers des destinations nouvelles, ce qui lui demande d’avoir la capacité d’innover. Cette connaissance et cette capacité sont plus importants que le suivi d’un processus ou la gestion d’un backlog.

## Une mission technique

Les utilisateurs primaires d’une équipe plateforme sont des développeurs. Sa mission principale est de leur **simplifier la vie** pour qu’ils puissent se concentrer sur les problèmes fonctionnels et techniques qu’ils ont à traiter au quotidien. L'équipe plateforme est en maîtrise profonde d’un outillage et sait délivrer ce dont les utilisateurs ont besoin de la meilleure façon pour que les équipes veulent utiliser la plateforme et qu’ils n’y soient pas juste contraints.

**Il ne s’agit pas de faire à leur place**. L'équipe plateforme délivre de la valeur pour que les équipes gagnent en autonomie sur leurs problèmes et qu’ils minimise les échanges avec d’autres équipes leur faisant perdre du temps. Elle doit éviter systématiquement de se mettre dans le chemin critique de l’utilisateur puisqu’elle maîtrise nécessairement moins son contexte.

**Il ne s’agit pas de cacher des problèmes**. Elle doit permettre aux équipes de prendre de la hauteur sur les problèmes auxquels ils sont confrontés et leur donner les armes pour les régler de manière plus efficace. Cela signifie que les utilisateurs doivent parler le langage technique sous-jacent au problème (“table”, “topic”, “migration”, “*identity provider*“, …) mais qu’elles ont des outils plus efficaces pour le manipuler.

On peut identifier un besoin d’une équipe plateforme lorsqu’on observe beaucoup de répétitions dans le code (phénomène de compensation) et peu d’abstraction et de réutilisabilité pour des fonctionnalités identiques. La responsabilité pour ce genre de fonctionnalités partagées et les types d’architectures possibles pour les applications ne sont plus des questions avec une telle équipe en place.

# Le fonctionnement

## L'équipe

On retrouve les mêmes rôles que dans des équipes projet, quoiqu’avec un périmètre souvent différent.

## Product Manager

De façon similaire à un Product Owner, il est responsable des risques liés à la valeur (est-ce que les utilisateurs gagneront à utiliser ces produits ?) mais il porte en plus le rôle sur la viabilité (est-ce que le produit a de la valeur pour mon entreprise ?) de la plateforme.

## Développeurs

Au sens large et englobant DevOps, Technical Lead et autres architectes. Ils sont responsables des risques liés à la faisabilité (est-ce qu’on sait faire, dans les temps, avec la technologie à notre disposition ?).

---

Des équipes plateformes que j’aime bien

## [Fly.io](http://fly.io/)

> 🎶 There are two kinds of platform companies 🎶 : the kind where you can sign up online and be playing with them in 5 minutes, and the kind where you can sign up online and get a salesperson to call and quote you a price and arrange a demo.
> 
> 
> 🎶 There are two kinds of platform companies 🎶 : the kind you can figure out without reading the manual, and the kind where publishers have competing books on how to use them, the kind where you can get professionally certified in actually being able to boot up an app on them.
> 
> 🎶 There are two kinds of platform companies 🎶 : the kind where you can get your Python or Rust or Julia code running nicely, and the kind where you find a way to recompile it to Javascript.
> 

## Sources

- [Platform Teams](https://blog.pragmaticengineer.com/platform-teams/) par Gergely Orosz
- [11 years hosting a SaaS](https://ghiculescu.substack.com/p/11-years-of-hosting-a-saas) par Alex Ghiculescu