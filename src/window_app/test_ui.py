import time
import sys
import platform
from PyQt5 import QtWidgets
from PyQt5 import QtGui
from PyQt5 import uic
from PyQt5 import QtCore
from PyQt5.QtCore import pyqtSlot
from win10toast import ToastNotifier
import threading

def notify():
    if( platform.system() == 'Windows' and platform.release() == '10'):
        toaster = ToastNotifier()
        toaster.show_toast("Touch On Screen", "Program is running in System Tray~")


class Form(QtWidgets.QDialog, QtWidgets.QSystemTrayIcon):
    def __init__(self, parent=None):
        QtWidgets.QDialog.__init__(self, parent)
        QtWidgets.QSystemTrayIcon.__init__(self,parent)
        self.ui = uic.loadUi("test.ui", self)
        self.setWindowIcon(QtGui.QIcon("test.png"))
        # self.ui.login_widget.hide()
        # self.ui.how_to_widget.hide()
        #print(self.login_widget.)
        #print(self.login_widget.widget_4)
        

    @pyqtSlot()
    def generate_num(self):
        #self.ui.main_widget.pw_label.setText('1234')
        self.ui.pw_label_2.setText('1234')

    def closeEvent(self, QCloseEvent):
        print("WindowCLoseEvent")
        
        noti = threading.Thread(target=notify)
        noti.start()
        print('뭐지...')
        #self.deleteLater()
        #QCloseEvent.accept()


    
class SystemTrayIcon(QtWidgets.QSystemTrayIcon):

    def __init__(self, icon, parent=None):
        QtWidgets.QSystemTrayIcon.__init__(self, icon, parent)
        print(parent)
        menu = QtWidgets.QMenu(parent)

        openAction = menu.addAction("Open")
        openAction.triggered.connect(parent.showNormal)

        exitAction = menu.addAction("Exit")
        exitAction.triggered.connect(app.quit)
        
        self.setContextMenu(menu)


        

if __name__ == '__main__':
    app = QtWidgets.QApplication(sys.argv)
    app.setQuitOnLastWindowClosed(False)
    w = Form()
    w.show()
    print(w)
    trayIcon = SystemTrayIcon(QtGui.QIcon("test.png"), w)
    trayIcon.show()
    sys.exit(app.exec())
    

#size = 365 305
