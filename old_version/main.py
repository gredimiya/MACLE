from PyQt6.QtWidgets import QApplication, QWidget, QMainWindow # Gardons QWidget
from PyQt6.QtCore import pyqtSlot
import sys
import Classement # Assurez-vous d'avoir bien Classement.py
import ui # Contient la classe Ui_Form

# C'est la classe qui hérite du conteneur visuel et héberge la logique.
class MainWindow(QWidget):
    def __init__(self):
        super().__init__()
        
        self.ui = ui.Ui_Form()
        self.ui.setupUi(self)

        # 1. Initialisation de la logique (Classement)
        # On utilise la valeur initiale du QSpinBox pour définir la taille du top.
        nb_places = self.ui.PlaceNumber.value()
        self.classement_logic = Classement.Classement(nb_places)
        
        # --- Connexions des signaux aux slots pour modifier le visuel ---
        
        # 2. Mise à jour du Titre en temps réel
        self.ui.SettingsTopTiltleText.textChanged.connect(self.update_top_title)
        
        # 3. Mise à jour du Sous-Titre en temps réel
        self.ui.SettingsTopSubtiltleText.textChanged.connect(self.update_sub_title)
        
        # 4. Connexion du bouton Ajouter
        self.ui.AddButton.clicked.connect(self.add_anime_to_ranking)
        
        # 5. Connexion de l'input pour changer la taille du Top
        self.ui.PlaceNumber.valueChanged.connect(self.update_ranking_size)
        

    # --- Méthodes (Slots) de mise à jour du visuel ---
    
    # Slot 1: Met à jour le QLabel du titre avec le contenu du QTextEdit
    @pyqtSlot()
    def update_top_title(self):
        new_title = self.ui.SettingsTopTiltleText.toPlainText()
        self.ui.TopTittle.setText(new_title)

    # Slot 2: Met à jour le QLabel du sous-titre avec le contenu du QTextEdit
    @pyqtSlot()
    def update_sub_title(self):
        new_sub_title = self.ui.SettingsTopSubtiltleText.toPlainText()
        self.ui.SubTitleLabel.setText(new_sub_title)
        
    # Slot 3: Met à jour la logique interne quand le nombre de places change
    @pyqtSlot(int)
    def update_ranking_size(self, new_size):
        # Ici, vous devriez implémenter la logique pour redimensionner 
        # ou recréer self.classement_logic si nécessaire, 
        # et mettre à jour la liste visible (TopList).
        print(f"Nouvelle taille de Top demandée: {new_size}")
        # self.classement_logic.nb_place = new_size 
        # ... et mise à jour de l'affichage ...

    # Slot 4: Gère l'ajout d'un élément
    @pyqtSlot()
    def add_anime_to_ranking(self):
        name = self.ui.NameAnimeText.toPlainText().strip()
        is_out = self.ui.DehorsLabel.isChecked()
        position = self.ui.PositionTop.value()

        if not name:
            return # Ne rien faire si le nom est vide

        if is_out:
            # Ajouter à la liste "Dehors"
            self.classement_logic.add_out(name) # Met à jour la logique
            self.ui.DehorsList.addItem(name)    # Met à jour le visuel
            print(f"Ajouté à Dehors: {name}")
        else:
            # Ajouter au Top
            # Note: Vous devrez mettre à jour TopList pour refléter 
            # l'ordre et le décalage faits par self.classement_logic.add_top()
            self.classement_logic.add_top(name, position) 
            self.update_top_list_visual() # Fonction pour reconstruire la liste TopList

        # Effacer l'input après l'ajout
        self.ui.NameAnimeText.clear()
        
    # Fonction de support pour reconstruire l'affichage du Top
    def update_top_list_visual(self):
        # Reconstruit la QListWidget TopList à partir des données de Classement
        self.ui.TopList.clear()
        for i, item_name in enumerate(self.classement_logic.top):
            # Afficher uniquement les éléments non vides
            if item_name:
                display_text = f"#{i+1}: {item_name}"
                self.ui.TopList.addItem(display_text)
            
        # Mettez aussi à jour la liste Dehors si des éléments ont été décalés
        self.ui.DehorsList.clear()
        for name in self.classement_logic.out:
            self.ui.DehorsList.addItem(name)


if __name__ == '__main__':
    app = QApplication(sys.argv)
    window = MainWindow()
    window.show()
    sys.exit(app.exec())

