# Projekt 2: Framework Fork-Join
**Przedmiot:** Programowanie współbieżne w języku JAVA

## O projekcie
Celem projektu jest implementacja i analiza wydajnościowa algorytmu rozmycia Gaussa (Gaussian Blur) z wykorzystaniem frameworka **Fork-Join**. Aplikacja pozwala na porównanie czasu przetwarzania obrazu w sposób sekwencyjny oraz zrównoleglony.

Projekt demonstruje:
1. **Podział problemu:** Rekurencyjne dzielenie obrazu na mniejsze fragmenty (Divide and Conquer).
2. **Zarządzanie wątkami:** Wykorzystanie klasy `ForkJoinPool` oraz `RecursiveAction`.
3. **Optymalizację:** Dobór optymalnego progu (threshold) podziału zadania.

## Instrukcja uruchomienia i testowania
Główną klasą aplikacji jest `GaussianBlur.java`. W obecnej wersji (Commit Inicjalny) program wykonuje rozmycie w sposób sekwencyjny, stanowiąc bazę do dalszej rozbudowy.

Aby przetestować aplikację:
1. Umieść plik obrazu w folderze projektu (np. `input.jpg`).
2. Uruchom klasę `GaussianBlur`.
3. Program wygeneruje plik `output.jpg` z nałożonym filtrem.

## Lista zadań (TODO)

### Faza 1: Architektura i bazowa logika
- [x] Przygotowanie struktury projektu.
- [x] Implementacja sekwencyjnego algorytmu splotu (convolution) dla rozmycia Gaussa.
- [x] Obsługa wczytywania i zapisywania plików graficznych (ImageIO).
- [ ] Stworzenie prostego GUI do podglądu obrazu przed i po operacji.

### Faza 2: Implementacja Fork-Join
- [ ] Implementacja klasy dziedziczącej po `RecursiveAction`.
- [ ] Logika podziału obrazu na pod-zadania (poziomy lub pionowy podział).
- [ ] Integracja z `ForkJoinPool`.
- [ ] Parametryzacja progu podziału (threshold) i promienia rozmycia (radius).

### Faza 3: Profilowanie i Analiza
- [ ] Wykonanie pomiarów czasu wykonania dla różnych rozmiarów obrazu (np. Full HD, 4K).
- [ ] Badanie wpływu wartości progu (threshold) na wydajność.
- [ ] Wygenerowanie wykresów obciążenia CPU w profilerze (VisualVM).
- [ ] Analiza stanów wątków w puli ForkJoinPool (worker threads).

### Faza 4: Dokumentacja
- [ ] Sformułowanie wniosków dotyczących zysku wydajnościowego.
- [ ] Opisanie zjawiska "work-stealing" w kontekście wykonanego zadania.
- [ ] Złożenie końcowego sprawozdania.

## Autorzy
- Mateusz Moskwin
- Beniamin Raczyński
- Monika Szczerba
- Kacper Marciniak
- Maciej Wojnowski
