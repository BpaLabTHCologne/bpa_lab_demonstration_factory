# Verwendet ein offizielles Python-Runtime-Image als Basis
FROM python:3.11

# Setzt das Arbeitsverzeichnis im Container
WORKDIR /warehouse_operations_app

# Kopiert die Dateien requirements.txt in das Arbeitsverzeichnis
COPY requirements.txt ./

# Installieren Sie alle benötigten Pakete
RUN pip install --no-cache-dir -r requirements.txt

# Kopieret den Quellcode in das Arbeitsverzeichnis
COPY . .

# Definieren Sie den Befehl zum Ausführen der Anwendung
CMD ["python", "./warehouse_operations_process_app.py"]