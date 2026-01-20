# MACLE - Anime Ranking Studio

**MACLE** est une application de bureau intuitive conçue pour créer, personnaliser et partager vos classements d'animes (Top). Développée en JavaFX, elle permet une gestion dynamique de vos listes via le glisser-déposer et une personnalisation poussée du rendu visuel.

---

## 🚀 Installation et Lancement (Windows)

L'application est distribuée sous forme d'image autonome (**dist**). Vous n'avez pas besoin d'installer Java sur votre machine pour l'utiliser.

1. **Téléchargez** l'archive `.zip` de l'application.
2. **Extrayez** le contenu de l'archive dans le dossier de votre choix.
4. Lancez l'exécutable **`MacleRanking.exe`**.

> **Note :** Si Windows SmartScreen affiche une alerte au premier lancement, cliquez sur *"Informations complémentaires"* puis *"Exécuter quand même"*.

---

## ✨ Fonctionnalités Principales

* **Gestion Dynamique :** Ajoutez des animes dans votre Top ou déplacez-les dans la section "Dehors".
* **Glisser-Déposer (Drag & Drop) :** Réorganisez votre classement intuitivement en faisant glisser les noms directement dans la grille.
* **Personnalisation Totale :**
    * Modification des titres et sous-titres en temps réel.
    * Réglage de la taille de la police, de la couleur, du gras et du contour pour chaque section (Titre, Top, Sous-Titre, Dehors).
    * Ajustement de l'écartement des colonnes du classement.
* **Design Visuel :**
    * Importez votre propre image de fond.
    * Gérez l'opacité du fond pour améliorer la lisibilité du texte.
* **Sauvegarde et Export :**
    * **JSON :** Sauvegardez votre classement et vos styles pour les reprendre plus tard.
    * **PNG :** Exportez votre rendu final en haute qualité pour le partager.

---

## 🛠️ Utilisation des Paramètres

Le panneau latéral droit (accessible via un défilement si nécessaire) regroupe tous les outils de création :
* **Ajouter un Anime :** Saisissez le nom, choisissez la position ou cochez "Dehors".
* **Paramètres Généraux :** Changez le nombre de places du Top (5, 10, 15 ou 20) et gérez l'image de fond.
* **Styles :** Utilisez les curseurs (sliders) pour ajuster les tailles et les sélecteurs de couleur. Chaque section est indépendante.

---

## 💻 Pour les Développeurs

Si vous souhaitez modifier le code ou compiler l'application vous-même :

### Prérequis
* **JDK 21** ou supérieur.
* **Maven** (ou utilisez le wrapper `./mvnw` inclus).

## 👤 Crédits

Application développée par @gredimiya.
