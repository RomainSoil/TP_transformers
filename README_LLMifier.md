# 🍽️ LLMifier — Recettes selon la météo

## 1. Présentation du projet

Cette application **Java console** propose automatiquement un **menu complet** (entrée, plat, dessert) en fonction :

- de la **météo actuelle** (via l’API *OpenWeatherMap*),
- de la **saison** (déduite de la date courante),
- des **préférences alimentaires de l’utilisateur**, fournies sous forme d’historique,
- en s’appuyant sur un **LLM local exécuté via Ollama**.

L’objectif est de montrer comment intégrer un **modèle de langage** dans une application Java, tout en tenant compte de **données contextuelles réelles**.

---

## 2. LLM testés et comparaison

Dans le cadre de la fonction `sample1()` (calcul simple) ainsi que pour la génération de menus, plusieurs modèles ont été testés.

TinyLlama — tinyllama-1.1B
Critère	 : Valeur
Taille : 	~1.1 milliard de paramètres
Poids disque : 	~600–700 Mo (selon quantization)
Énergie consommée : 	Faible
Vitesse	 : Très rapide
Qualité observée : 	Faible

Observations :
- réponses souvent imprécises,
- erreurs fréquentes sur des calculs simples,
- non-respect des contraintes (préférences alimentaires, format imposé),
- résultats instables et peu fiables.

Conclusion :
TinyLlama est rapide et léger, mais la qualité est insuffisante pour une application nécessitant précision et respect strict des consignes.
Il n’est pas adapté à ce projet.
---


### 🔹 Phi-4 — `phi-4` / `phi-4-mini`
Critère : 	Valeur
Taille : ~3.8 milliards de paramètres
Poids disque : 	~2 à 3 Go
Énergie consommée : Modérée
Vitesse : Rapide
Qualité observée :	Moyenne à bonne

Observations :
- résultats corrects sur des calculs simples,
- réponses globalement cohérentes,
- justifications intéressantes,
- mais non-respect fréquent des préférences utilisateur (ex : ingrédients interdits),
- parfois incomplet (entrée ou dessert manquant).

Conclusion :
Phi-4 constitue un bon compromis technique, mais son manque de rigueur dans le respect des contraintes limite son utilisation pour ce projet.


### GPT-OSS — `gpt-oss:120b-cloud`
Critère	 : Valeur
Taille : 	~120 milliards de paramètres
Poids disque : 	Très élevé (exécution cloud)
Énergie consommée  :	Très importante
Vitesse	: Moyenne
Qualité observée : 	Excellente

Observations :
- réponses très cohérentes et structurées
- respect strict du format demandé (entrée / plat / dessert),
- excellente prise en compte des préférences utilisateur,
- justifications claires et pertinentes,
- très bonne compréhension du contexte météo et saisonnier.

Conclusion :
Même si GPT-OSS est très volumineux et énergivore, il est de loin le modèle le plus performant testé dans ce projet.
Il fournit les réponses les plus fiables, complètes et pertinentes.
---

## 3. Paramétrage de la température

La **température** est un paramètre contrôlant le degré de **créativité** du modèle.

- `temperature = 0.0` → réponses déterministes (calculs, faits exacts)
- `temperature ≈ 0.6 – 0.8` → réponses naturelles pour la génération de menus
- `temperature > 1.0` → créativité accrue mais risque d’erreurs

Dans ce projet :
- une **température basse** est utilisée pour `sample1()` (calcul),
- une **température modérée** est utilisée pour les recettes.

---

## 4. Réponses aux questions théoriques

### Qu’est-ce que l’attention ?

L’attention est un mécanisme qui permet au modèle de se concentrer sur les parties les plus pertinentes d’une séquence d’entrée.  
Elle attribue un poids aux mots (tokens) afin de mieux capturer le contexte et les relations entre eux.

---

### Qu’est-ce qu’un encodeur et un décodeur ?  
**A-t-on toujours besoin des deux ?**

- L’**encodeur** transforme l’entrée en une représentation interne.
- Le **décodeur** génère la sortie à partir de cette représentation.

Les modèles modernes de type GPT utilisent uniquement un **décodeur**, ce qui est suffisant pour la génération de texte.  
On n’a donc **pas toujours besoin des deux**.

---

### Qu’est-ce que la température ?

La température modifie la distribution de probabilité lors de la génération des tokens :

- basse température → réponses précises et stables,
- haute température → réponses plus variées mais moins fiables.

C’est un compromis entre **exactitude** et **créativité**.

---

## 5. Conclusion

Ce projet met en évidence l’importance du **choix du modèle de langage** :

- les modèles trop petits manquent de précision,
- les modèles trop grands sont énergivores et peu adaptés,
- un modèle intermédiaire comme **Phi-3** offre un excellent compromis.

L’intégration d’un LLM local avec des données météo réelles permet de produire des réponses **contextualisées, pertinentes et personnalisées**.
