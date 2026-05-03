# Projekt 2: Framework Fork-Join
**Przedmiot:** Programowanie współbieżne w języku JAVA

## O projekcie
Celem projektu jest implementacja i analiza wydajnościowa algorytmu rozmycia Gaussa (Gaussian Blur) z wykorzystaniem frameworka **Fork-Join**. Aplikacja pozwala na bezpośrednie porównanie czasu przetwarzania obrazu w sposób sekwencyjny oraz zrównoleglony w ramach jednego pliku.

Projekt demonstruje:
1. **Podział problemu:** Rekurencyjne dzielenie obrazu (poziome cięcie na paski) za pomocą schematu Divide and Conquer.
2. **Zarządzanie wątkami:** Wykorzystanie klasy `ForkJoinPool` oraz zadania dziedziczącego po `RecursiveAction`.
3. **Optymalizację:** Dobór optymalnego progu (threshold) podziału zadania.

## Instrukcja uruchomienia i testowania
Główną klasą aplikacji jest `GaussianBlur.java`. Została ona wyposażona w przełącznik trybu działania, co ułatwia testy wydajnościowe.

Aby przetestować aplikację:
1. Upewnij się, że w folderze `assets/` znajduje się duże zdjęcie testowe (np. `test_image.jpg` w rozdzielczości 4K dla najlepszych wyników pomiarowych).
2. Wewnątrz metody `main` w pliku `GaussianBlur.java` znajdź zmienną `MODE`:
   - `int MODE = 0;` – Uruchamia klasyczne, sekwencyjne przetwarzanie obrazu (jeden wątek).
   - `int MODE = 1;` – Uruchamia współbieżne przetwarzanie wykorzystujące pulę `ForkJoinPool`.
3. Po uruchomieniu program wypisze w konsoli czas wykonania w milisekundach i wygeneruje nałożony filtr w pliku `output.jpg`.

## Lista zadań (TODO)

### Faza 1: Architektura i bazowa logika
- [x] Przygotowanie struktury projektu (utworzenie folderu `assets`, plik `.gitignore`).
- [x] Implementacja sekwencyjnego algorytmu splotu (convolution) dla rozmycia Gaussa.
- [x] Obsługa wczytywania i zapisywania plików graficznych (ImageIO).
- [ ] Stworzenie prostego GUI do podglądu obrazu przed i po operacji (Opcjonalnie).

### Faza 2: Implementacja Fork-Join
- [x] Implementacja klasy `BlurTask` dziedziczącej po `RecursiveAction`.
- [x] Zaimplementowanie logiki podziału problemu na mniejsze pod-zadania (cięcie na wiersze).
- [x] Integracja z `ForkJoinPool` i wykorzystanie metody `invokeAll`.
- [x] Wdrożenie parametru określającego próg podziału (`THRESHOLD`).

### Faza 3: Profilowanie i Analiza
- [ ] Uruchomienie aplikacji z podpiętym profilerem (np. VisualVM, JProfiler).
- [ ] Wykonanie pomiarów czasu wykonania dla obu trybów (`MODE=0` vs `MODE=1`) na dużym obrazie (np. 4K).
- [ ] Przeprowadzenie eksperymentów polegających na zmianie wartości `THRESHOLD` (np. 10, 100, 1000) i odnotowanie wpływu na czas wykonania.
- [ ] Zrzuty ekranu z profilera (zużycie procesora w czasie dla obu trybów).

### Faza 4: Dokumentacja
- [ ] Sformułowanie ostatecznych wniosków z analizy porównawczej.
- [ ] Opisanie zjawiska "work-stealing" występującego we frameworku Fork-Join na podstawie obserwacji profilera.
- [ ] Złożenie końcowego sprawozdania lub przygotowanie prezentacji.

## Autorzy
- Mateusz Moskwin
- Beniamin Raczyński
- Monika Szczerba
- Kacper Marciniak
- Maciej Wojnowski