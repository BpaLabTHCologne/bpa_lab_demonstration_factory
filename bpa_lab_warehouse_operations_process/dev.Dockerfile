FROM python:3.11

WORKDIR /warehouse_operations_app

COPY requirements.txt ./

RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD ["python", "./warehouse_operations_process_app.py"]