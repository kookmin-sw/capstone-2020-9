from tkinter import *
from PIL import Image, ImageTk
import pyautogui
import time

WIDTH, HEIGHT = pyautogui.size()
size = 250
root = Tk()
root.wm_attributes("-alpha", '0.6')
root.overrideredirect(1)
root.geometry("{}x{}+{}+{}".format(size, size, int((WIDTH-size)/2), int((HEIGHT-size)/2)) )

#img = ImageTk.PhotoImage(Image.open('return.png').resize((size,size)))
photo = PhotoImage(file = 'locked-padlock.png')
label= Label(root, image=photo)
label.pack()

root.after(1000, lambda: root.destroy())
root.mainloop()

