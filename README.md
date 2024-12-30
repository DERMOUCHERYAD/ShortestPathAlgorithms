# Recherche du Plus Court Chemin

Ce projet implémente deux algorithmes de recherche du plus court chemin dans un graphe pondéré : **Dijkstra** et **A\***. Il permet de comparer leurs performances sur des cartes générées sous forme de fichiers texte.

---

## Objectif

- Implémenter et comparer les algorithmes **Dijkstra** et **A\***.
- Visualiser les résultats et les statistiques comme :
  - Nombre de nœuds explorés.
  - Temps total du chemin.
  - Temps d'exécution de l'algorithme.
- Sauvegarder le chemin trouvé dans un fichier `out.txt`.

---

## Fonctionnalités

1. Chargement d'une carte depuis un fichier `graph.txt` (format prédéfini).
2. Choix entre les algorithmes **Dijkstra** et **A\*** via un menu interactif.
3. Affichage du chemin sur une interface graphique.
4. Sauvegarde du chemin calculé dans le fichier `out.txt`.

---

## Fichiers Importants

- **[`App.java`](App.java)** : Le fichier principal qui contient l'implémentation et l'exécution.
- **[`WeightedGraph.java`](WeightedGraph.java)** : Définition des structures de graphe (sommets, arêtes, etc.).
- **[`graph.txt`](graph.txt)** : Le fichier contenant la définition de la carte.
- **[`out.txt`](out.txt)** : Fichier généré contenant les indices des nœuds composant le chemin trouvé.

---
## Compilation et Exécution
- Compiler le projet :
javac -d bin MainApp/*.java

- Exécuter le projet :
java -cp bin MainApp.App

## Exemple d'Exécution
Une fois l'exécution terminée, vous verrez dans la console :

Number of nodes explored: 4119
Total time of the path: 336.0
Algorithm runtime: 63.958 s
Chemin écrit dans out.txt !


 
## Format du Fichier `graph.txt`

Voici un exemple du format attendu :

```txt
==Metadata==
=Size=
nlines=50
ncol=100
=Types=
G=1
green
W=1000
gray
==Graph==
GGGGGWWGGGGGGGG
GGGGGWGGGGGGGGG
...
==Path==
Start=0,0
Finish=36,63
```
## Auteur
Sylvain Lobry : Création initiale du TP.
Mohammed Ryad Dermouche

