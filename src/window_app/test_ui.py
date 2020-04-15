import time
import sys
from PyQt5 import QtWidgets
from PyQt5 import QtGui
from PyQt5 import uic
from PyQt5 import QtCore
from PyQt5.QtCore import pyqtSlot
 

class Form(QtWidgets.QDialog):
    def __init__(self, parent=None):
        QtWidgets.QDialog.__init__(self, parent)
        self.ui = uic.loadUi("tos.ui", self)
        self.ui.login_widget.hide()
        self.ui.how_to_widget.hide()
        self.ui.show()
        #   print(self.login_widget.)
        #print(self.login_widget.widget_4)
        

    @pyqtSlot()
    def generate_num(self):
        self.ui.pw_label_2.setText('1234')

    

        

if __name__ == '__main__':
    app = QtWidgets.QApplication(sys.argv)
    w = Form()
    sys.exit(app.exec())

#size = 365 305
