import vosk
import pyaudio
import threading

class VoiceRecognizer:
    def __init__(self, model_path):
        # モデルのロード
        self.model = vosk.Model(model_path)
        self.recognizer = vosk.KaldiRecognizer(self.model, 16000)
        self.is_recognizing = False
        self.p = pyaudio.PyAudio()

    def start_recognition(self, callback):
        self.is_recognizing = True
        self.stream = self.p.open(format=pyaudio.paInt16,
                                  channels=1,
                                  rate=16000,
                                  input=True,
                                  frames_per_buffer=4000)
        threading.Thread(target=self._recognize, args=(callback,)).start()

    def _recognize(self, callback):
        while self.is_recognizing:
            data = self.stream.read(4000)
            if self.recognizer.AcceptWaveform(data):
                result = self.recognizer.Result()
                text = result.get('text', '')
                callback(text)

    def stop_recognition(self):
        self.is_recognizing = False
        self.stream.stop_stream()
        self.stream.close()
        self.p.terminate()
