# Finance Tracker - Application Android de Gestion des Finances

![Build Status](https://github.com/madijason/AndroidApp/actions/workflows/build.yml/badge.svg)

💰 Une application Android moderne conçue pour vous aider à définir et suivre vos objectifs d'épargne.

## ✨ Fonctionnalités

- **Définir un objectif d'épargne** : Créez votre objectif avec un montant cible personnalisé
- **Suivre votre progression** : Visualisez votre progression avec une barre animée en temps réel
- **Gérer les transactions** : Ajoutez des dépôts ou des retraits avec descriptions
- **Historique complet** : Consultez toutes vos transactions avec horodatage
- **Supprimer des transactions** : Annulez facilement une transaction en cas d'erreur
- **Persistance des données** : Toutes vos données sont sauvegardées localement avec DataStore
- **Design moderne** : Interface Material Design 3 avec support du mode sombre
- **Animations fluides** : Expérience utilisateur soignée avec animations

## 🛠️ Technologies

- **Kotlin** : Langage de programmation moderne pour Android
- **Jetpack Compose** : UI déclarative moderne
- **Material Design 3** : Design système le plus récent de Google
- **DataStore** : Stockage de données asynchrone
- **ViewModel & StateFlow** : Gestion d'état réactive
- **Gradle Kotlin DSL** : Configuration de build moderne

## 📱 Captures d'écran

L'application offre :
- 🎯 Une carte d'objectif avec progression visuelle
- ➕ Des dialogues intuitifs pour ajouter objectifs et transactions
- 📊 Un historique coloré des transactions (vert pour dépôts, rouge pour retraits)
- ✅ Un indicateur de réussite quand l'objectif est atteint

## 🚀 Compilation

### Prérequis

- Android Studio Hedgehog | 2023.1.1 ou supérieur
- JDK 17
- Android SDK 34

### Compilation locale

```bash
# Cloner le dépôt
git clone https://github.com/madijason/AndroidApp.git
cd AndroidApp

# Compiler le debug APK
./gradlew assembleDebug

# Compiler le release APK
./gradlew assembleRelease
```

Les APK seront générés dans `app/build/outputs/apk/`

## 🤖 GitHub Actions

Le dépôt inclut un workflow GitHub Actions qui :
- Compile automatiquement l'application à chaque push sur `main`
- Génère les APK debug et release
- Met les APK à disposition en tant qu'artifacts téléchargeables

Vous pouvez télécharger les APK depuis l'onglet **Actions** de ce dépôt après chaque build réussi.

### Télécharger les APK

1. Allez dans l'onglet [Actions](https://github.com/madijason/AndroidApp/actions)
2. Cliquez sur la dernière exécution réussie (badge vert)
3. Scrollez jusqu'en bas pour voir les artifacts
4. Téléchargez `finance-tracker-debug` ou `finance-tracker-release`

## 📝 Architecture

```
app/
├── data/
│   ├── Transaction.kt         # Modèle de transaction
│   ├── SavingsGoal.kt         # Modèle d'objectif d'épargne
│   └── SavingsRepository.kt   # Gestion de la persistance
├── ui/
│   ├── MainScreen.kt          # Écran principal
│   ├── SavingsViewModel.kt    # ViewModel
│   ├── components/
│   │   ├── GoalDialog.kt      # Dialogue d'objectif
│   │   └── TransactionDialog.kt # Dialogue de transaction
│   └── theme/
│       ├── Theme.kt           # Thème Material 3
│       └── Type.kt            # Typographie
└── MainActivity.kt        # Point d'entrée
```

## 📄 Licence

Ce projet est un exemple éducatif. N'hésitez pas à l'utiliser et le modifier selon vos besoins.

## 👤 Auteur

Développé par [Jason Madi](https://github.com/madijason)

---

**Bon suivi de vos finances ! 💰✨**
