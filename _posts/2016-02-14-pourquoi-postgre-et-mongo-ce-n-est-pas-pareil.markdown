---
layout: post
title:  "Pourquoi Postgre et Mongo ce n'est pas pareil ?"
date:   2016-02-14
categories: base de donnée
comments: true
---

Les bases de données relationelles ont été utilisées pendant de nombreuses années pour stocker de la donnée. Elles ont apporté de nombreux bénéfices dont la possibilité de faire persister de la donnée, certes, mais aussi de gérer les transactions concurrentes qui étaient bloquantes avec l'utilisation du stockage en fichier. Ces bases de données venaient aussi avec quelques inconvénients, et le plus important de tous était qu'on assemblait nos structures de données en un seul objet, mais qu'il devait être sauvegarder dans des tables différentes. Ce problème est connu sous le terme d'[inadéquation d'impédence](https://www.wikiwand.com/en/Object-relational_impedance_mismatch), c'est à dire le fait que l'on ait plusieurs moyen de voir les choses et que l'on ait à les harmoniser pour pouvoir les utiliser.

Ce problème, les gens s'y sont plutôt fait, et des frameworks ont été construits pour le gérer. Néanmoins, après près de 20 ans d'utilisation de bases de données relationnelles (on est dans les années 2000 là), un besoin de plus en plus important en scalabilité a créé de nouveaux challenges pour ces bases de données. Elles devenaient massives et coûteuses, et une des solutions qui a été trouvée a été de les séparer en plusieurs petites machines qui contiendraient une partie de la donnée. Mais les bases de données relationnelles n'ont pas vraiment été créées avec cette utilisation sur des clusters en tête: c'est une solution qui manquait de scalabilité.

On commençait à avoir plusieurs problèmes gênant avec ces bases de données, les gens intelligents ont réfléchis et est né le mouvement NoSql. La réponse au problème était simple: il fallait créer une nouvelle génération de bases peu coûteuses, à haute performance. Cette nouvelle génération devait pouvoir s'adapter aux différentes implémentations et exigences des applications du web, des entreprises et des systèmes de cloud computing. Concrètement, une base de donnée complète doit aborder aujourd'hui le problème de la mise à jour de la donnée sur différentes machines d'un clusters, mais aussi savoir traiter rapidement de gros volumes de données ainsi que de requêtes, tout en garantissant un support pour les différents formats de données existant et qui seront créés.

La plupart des technologies qui ont émergées depuis se spécialisent dans quelques uns de ces aspects en sacrifiant les autres. On parle du [théorème CAP](http://robertgreiner.com/2014/08/cap-theorem-revisited/), pour dire qu'il est impossible pour un système informatique de fournir à la fois des garanties de consistence de la donnée (si une opération B a débuté après qu'une opération A ait été effectuée avec succès, alors l'opération B doit voir le système dans un état similaire ou plus récent encore qu'il l'était à la complétion de l'opération A), de disponibilité (que chaque requête puisse recevoir une réponse en fonction de sa réussite ou non) et de tolérence au partitionnement (que le système continue à fonctionner en dépit des problèmes de réseau ayant lieu entre les différentes partitions).


## Revenons à nos moutons

L'approche à avoir aujourd'hui en ce qui concerne les bases de données nécessite ainsi une évaluation concrète des besoins de l'applications que l'on souhaite développer, avant de pouvoir choisir l'outil qui correspond à nos besoins.

Nous comparons ici deux bases de donnée:

 * [MongoDB](https://www.mongodb.org/), qui est un système de gestion de base de données orienté documents, répartissable sur un nombre quelconque d'ordinateurs et ne nécessitant pas de schéma prédéfini de donnée. On est ici dans du NoSQL comme vous l'avez compris.
 * [PostgreSQL](http://www.postgresqlfr.org/) qui est un système de gestion de base de données relationnelles qui a la particularité de laisser le droit à l'utilisateur de créer ses propres types, fonctions, ou utiliser l'héritage de types entre autres.

Les dernières versions de PostgreSQL permettent de nombreuses libertées à l'utilisateur qu'on ne trouvait auparavant que dans les bases de données NoSQL. Tout d'abord, il supporte les schémas de données dynamiques. En effet, Mongo stocke sa donnée sous le format BSON, un dérivé du format Json, qui permet un stockage de la donnée sous forme d'objets. PostgreSQL supporte depuis peu un nouveau type, Json, qui lui permet donc de faire de même. Un contrôle est fait sur la donnée en input ce qui permet notamment de la différencier du simple champ texte. Ainsi ces deux gestionnaires de base de donnée offrent un support des documents à donnée hiérarchique ainsi que des données de type clef-valeur.

Le premier point de blocage historique que nous avons vu plus haut cesse alors d'exister. En effet, puisqu'il est possible de stocker sa donnée dans une table de manière hiérarchisée, sous forme de document, l'inadéquation d'impédence n'est plus un problème. Venons en alors au second problème que nous avions rencontré.


## La scalabilité

Les serveurs de base de données peuvent travailler de concert en permettant à un second serveur de prendre rapidement la main si le permier serveur tombe (on parle de Haute Accessibilité) mais aussi pour permettre à plusieurs serveurs de servir la même donnée (on parle de Répartition de charge). Cela est généralement plutôt simple à mettre en place dans un cas de lecture unique. Mais bien sûr, les systèmes actuels ont un besoin mixe en lecture et en écriture de donnée. Il se trouve que le problème d'écriture de la donnée est bien plus compliqué, puisque la donnée écrite doit être propagée aux autres serveurs pour que les requêtes de lectures futures puisse rendre de la donnée à jour.

MongoDB a été conçu avec des problématiques de scalabilité en tête, de manière à être supportée sur des machines plutôt petites réparties sur un cluster. Il s'agit d'une base de donnée qui est ainsi [très simple à sharder](https://docs.mongodb.org/manual/tutorial/deploy-shard-cluster/). Sharder, c'est un mot anglais pas super simple à traduire (ça vient de "shard" que l'on peut traduire comme "tesson", c'est à dire un débris d'objet en céramique) qui signifie dans notre cas partitionner de manière horizontale notre donnée. En d'autres termes, cela signifie prendre une grosse base de donnée monolithique, la découper en petite parties sur plusieurs serveurs et faire tourner tout ça en parallèle.

Une telle approche a de nombreux avantages améliorant grandement les performance de notre système. Sans rentrer dans les détails, cela permet une diminution du nombre de ligne dans chaque table et donc une diminution de la taille des index, ce qui améliore généralement les performances. Cela permet aussi de distribuer la donnée sur plusieurs serveurs et donc la répartition de la charge, améliorant de même les performances de notre base de données. Bref, MongoDB est très bon dans ce domaine.


PostgreSQL excèle sur machine unique, [il explose plutôt les performances de MongoDB](http://www.enterprisedb.com/postgres-plus-edb-blog/marc-linster/postgres-outperforms-mongodb-and-ushers-new-developer-reality) et permet une scalabilité verticale (ajouter des colonnes pour stocker plus de données) triviale. Malheureusement, cette scalabilité est rapidement limitée et le passage à une scalabilité horizontale (stocker de nouvelles lignes sur d'autres machines), est, bien que réalisable, bien plus compliqué. Bien évidemment il existe de nombreuses stratégies pour y parvenir; mais de même que les bases NoSQL se spécialisent, ces solutions ont chacune leurs avantages et leurs défauts, et il est nécessaire de choisir le bon outil pour la bonne utilisation. Une des plus connues est le système de réplication "un maître, plusieurs esclaves" qu'implémente par exemple [Slony-I](http://www.slony.info/) de manière asynchrone. Globalement, toute requête en écriture sera dirigée vers une machine "maître" qui va la diffuser vers tous les serveurs impactés par cette requête. Slony-I est relativement [compliqué à mettre en place](http://get.enterprisedb.com/docs/Tutorial_All_PP_Slony_Replication.pdf), et un certain lag est remarquable (entre 1 et 3 secondes) entre le maître et les esclaves, ce qui peut donc causer, pour certains utilisateurs, une inconsistence de donnée. Cela n'est qu'une solution parmi tout ce qui est possible ([PGCluster](http://pgcluster.projects.pgfoundry.org/), DBMirror, [Sequoia](https://sourceforge.net/projects/sequoiadb/), ...), mais la plupart de ces solutions ne sont plus vraiment maintenus.

Ainsi, PostgreSQL n'arrive pas à la cheville de MongoDB en matière de scalabilité, créant d'énorme problèmes de performances.

## En conclusion

Nous avons vu les similarités qui existaient entre ces deux systèmes de gestion de base de données, mais aussi les différences qui les opposaient. MongoDB offre une scalabilité aisé, tandis que PostgreSQL ne peut se contenter que de proposer un ersatz de scalabilité via des outils complexes d'utilisation. Ce problème peut être résolu en couplant PostgreSQL à des systèmes d'indexing comme ElasticSearch: il est alors utilisé comme source de vérité et permet d'alimenter ElasticSearch pour une performance en lecture impressionante.




#### SOURCES:

 * [Introduction to NoSQL • Martin Fowler](https://www.youtube.com/watch?v=qI_g07C_Q5I)
 * [Please stop calling databases CP or AP • Martin Kleppmann](https://martin.kleppmann.com/2015/05/11/please-stop-calling-databases-cp-or-ap.html)
 * [Everything you need to know about sharding • Dylan Tong](https://www.mongodb.com/presentations/webinar-everything-you-need-know-about-sharding?jmp=docs)
