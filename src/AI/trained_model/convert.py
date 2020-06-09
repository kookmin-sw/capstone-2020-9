//버젼 문제로 구글 코랩에서 변환

from google.colab import drive
drive.mount('/content/gdrive/')

import tensorflow as tf

model  = tf.keras.models.load_model('model.h5')

converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

open("model.tflite", "wb").write(tflite_model)
