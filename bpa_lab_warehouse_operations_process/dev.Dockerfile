FROM python:3.11-slim

WORKDIR /warehouse_operations_app

COPY requirements.txt ./

RUN pip install --no-cache-dir -r requirements.txt \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

COPY . .

CMD ["python", "./warehouse_operations_process_app.py"]