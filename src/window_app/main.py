from socket import *
import threading
import time
import pyautogui
import webbrowser
import sys
import platform
from PyQt5 import QtWidgets
from PyQt5 import QtGui
from PyQt5 import uic
from PyQt5 import QtCore
from PyQt5.QtCore import pyqtSlot
from win10toast import ToastNotifier
import os
 

def point_on_screen(recvData):
    try:
        x_ratio, y_ratio = recvData.split(',')
        point_x = WIDTH * float(x_ratio)
        point_y = HEIGHT * float(y_ratio)

        print("좌표 : {}, {}".format(point_x,point_y) )
        #pyautogui.click(x=point_x, y=point_y)
    except:
        pass


def make_connection():
    port = 8081

    clientSock = socket(AF_INET, SOCK_STREAM)
    #clientSock.connect(('127.0.0.1', port))
    clientSock.connect(('15.164.116.157', port))
    

    print('접속 완료')

    sendData = 'com'
    clientSock.send(sendData.encode('utf-8'))

    password = clientSock.recv(1024).decode('utf-8')

    #화면에 비밀번호 보여주기 
    print("pw : " + password)
    return password, clientSock


def pointing_start(sock):
    # 좌표값 
    recvData = ''
    while True:
        recvData = sock.recv(1024).decode('utf-8')
        if( recvData == 'test' ) :
            continue
        elif( recvData == 'disconnected with other device'):
            sock.close()
            break # 재접속 여부 확인 
        elif( recvData == 'closed' ):
            sock.close()
            break

        print('입력 :', recvData)  # '0.7, 0.5'
        
        p = threading.Thread(target = point_on_screen, args=(recvData,), daemon=True )
        p.start()
    
    return recvData

def connectionStart(sock, QDialog):
    #연결 성공 
    while True:
        recvData = sock.recv(1024).decode('utf-8')
        if( recvData == 'Connected' ):
            QDialog.ui.status.setText('연결되었습니다')
            QDialog.ui.pw_label_2.setText('')
            print(recvData)
            break

    res = pointing_start(sock)

    if( res == 'disconnected with other device'):
        QDialog.ui.status.setText('연결이 종료되었습니다. \n다시 연결하려면 번호를 다시 생성해 주세요')
        QDialog.ui.pw_label_2.setText('')
    
def notify():
    if( platform.system() == 'Windows' and platform.release() == '10'):
        toaster = ToastNotifier()
        toaster.show_toast("Touch On Screen", "Program is running in System Tray~")


class Form(QtWidgets.QDialog):
    def __init__(self, parent=None):
        QtWidgets.QDialog.__init__(self, parent)
        self.ui = uic.loadUi("tos_v1.ui", self) #tos.ui
        self.ui.setWindowTitle('Touch On Screen')
        #self.ui.login_widget.hide()
        #self.ui.how_to_widget.hide()

    # def clearCountdownTime(self):
    #     self.countdown_time = 5

    # def countAndMinimization(self):    
    #     time.sleep(1)
    #     self.ui.status.setText('\n {}초뒤 최소화 됩니다.'.format(self.countdown_time))

    #     self.countdown_time -= 1
    #     if( self.countdown_time == 0 ):
    #         self.ui.status.setText('연결되었습니다')
    #         #self.ui.status.setText('')
    #         self.showMinimized()
    #         return

    #     countdown = threading.Thread(target=self.countAndMinimization)
    #     countdown.start()


    # make connection & generate number
    # 입력 들어오면 gui
    @pyqtSlot()
    def generate_num(self): # btn 
        #self.ui.pw_show.setText("1234")

        pw, sock = make_connection()
        #화면에 pw 보여주기 gui
        self.ui.pw_label_2.setText(pw)
        self.ui.status.setText('연결할 장비에 아래 비밀번호를 입력하세요.')#\n 프로그램을 최소화하여 사용하세요.')
        #self.clearCountdownTime()
        waiting = threading.Thread(target=connectionStart, args=(sock,self))
        waiting.start()

        # countdown = threading.Thread(target=self.countAndMinimization)
        # countdown.start()

    @pyqtSlot()
    def how_to(self): #btn
        webbrowser.open("https://github.com/kookmin-sw/capstone-2020-9")

    def closeEvent(self, QCloseEvent):
        print("WindowCLoseEvent")
        noti = threading.Thread(target=notify)
        noti.start()

    
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


        


#fixed size = 365 305        
if __name__ == '__main__':
    WIDTH, HEIGHT = pyautogui.size()  
    print('width={0}, height={1}'.format(WIDTH, HEIGHT))

    clientSock = socket
    app = QtWidgets.QApplication(sys.argv)
    app.setQuitOnLastWindowClosed(False)
    w = Form()
    w.show()

    trayIcon = SystemTrayIcon(QtGui.QIcon("Logo.png"), w)
    trayIcon.show()
    app.exec()
    os._exit(0)
