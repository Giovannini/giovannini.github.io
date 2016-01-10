---
layout: post
title:  "[REDACTION EN COURS] JEST pour tester une application React"
date:   2016-01-04
categories: javascript, test, jest
comments: true
---

[JEST](https://facebook.github.io/jest/) est un framework permettant d'effectuer des tests unitaires en Javascript "sans peine". Il utilise un système d'assertion plutôt familier, se charge de mocker automatiquement les modules chargés et lance les différents tests exécutés en parallèle pour plus de vélocité. De plus, il a été développé par Facebook, tout comme [React](https://facebook.github.io/react/). Il est souvent bon d'utiliser l'outil de test provenant de l'éditeur lorsqu'on utilise un librairie, c'est pourquoi je me penche aujourd'hui sur les tests via JEST pour une application de type React JS.

## Mise en place d'un environnement de test
