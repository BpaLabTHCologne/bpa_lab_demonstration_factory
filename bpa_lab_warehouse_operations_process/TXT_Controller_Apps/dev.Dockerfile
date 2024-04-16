FROM python:3.11-slim

WORKDIR /highbay_robot_app

COPY requirements.txt ./

RUN pip install --no-cache-dir -r requirements.txt \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

COPY . .

CMD ["python", "./highbay_robot_app.py"]