---
layout: post
title:  "Clean code: Fonctions"
date:   2017-01-21
categories: clean code, functions
comments: true
---

Les fonctions sont la première ligne rendant un programme organisé.
Il y a certaines règles de conduite que l'on peut suivre pour les écrire correctement.

Lorsqu'on regarde une fonction dans un programme que l'on ne connaît pas, il est compliqué de comprendre tous les détails.
Néanmoins, il est possible de se faire une idée générale de ce qu'elle fait.
Ce n'est malheureusement pas vrai dans tous les cas, et nous allons tenter de voir ce qui fait qu'une fonction est facile à lire et à comprendre.
Comment est-ce qu'une fonction peut communiquer l'intention qu'elle porte ?
Quels attributs peut-on donner à une fonction pour qu'un lecteur occasionnel puisse comprendre le programme dans lequel elle est appelée ?

# Une fonction doit être courte

Il est compliqué de trouver des preuves sur les assertions que je vais donner.
Peu de recherches existent démontrant qu'écrire des fonctions courtes sont meilleures que les fonctions longues.

## Courte comment ?

## Une implication
// Block and indenting


# Une fonction doit faire une seule chose

## Comment dire qu'une fonction fait une seule chose ?

### TO method

### Renommer les différentes parties de la fonction

### Découper en sections

## Récupérer des erreurs, c'est une chose


## Un seul niveau d'abstraction

Pour être certains qu'une fonction ne fait qu'une seule chose, on doit s'assurer que les lignes de notre fonction ont toutes le même niveau d'abstraction.
Mélanger les niveaux d'abstraction va rendre l'utilisateur confus: il est compliqué de savoir si une expression est un concept essentiel ou juste un détail.

Une des méthodes pour n'avoir qu'un seul niveau d'abstraction est de lire le code de bas en haut.

## Un nommage descriptif

On sait qu'on travaille sur du code propre si chaque fonction qu'on lit fait à peu près ce qu'on pensait qu'elle fait.

- Il ne faut pas avoir peur des noms longs
- Il ne faut pas avoir peur de prendre du temps pour choisir un bon nom

## Pas d'effet de bord
Un effet de bord, c'est un mensonge que fait ta fonction.
Elle promet de faire une chose, mais elle fait aussi un petit truc caché en plus.
Des fois ce sont des changements aux paramètres de la classe, d'autres fois aux paramètres passés à la fonction.


# Le bon nombre d'arguments

Limiter le nombre de paramètres à une fonction est très important car cela permet de tester la fonction plus facilement.
Si on en a plus de trois, on se retrouve avec une explosion de combinaisons à tester.

## Eviter les arguments drapeaux

Passer un booléen dans une fonction est une pratique qui n'est pas recommandée.
Cela complique la signature de la fonction et surtout, cela indique fortement que la fonction fait plusieurs choses: une si le flag est à vrai, une autre si il est à faux.

## Passer des objets en arguments
Quand une fonction a besoin de plus de deux ou trois arguments, il y a des chances pour qu'on puisse regrouper ces arguments dans une classe.

## Pas d'arguments en sortie
Dans une fonction, on voit généralement les arguments comme des entrées.
Un argument qui est traité comme une sortie demande d'y regarder à deux fois et complexifie la lecture de la fonction.
En fait, on est obligé de lire la signature de la fonction pour vraiment saisir l'intention.


# Don't Repeat Yourself
