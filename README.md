## KoPI
<img src="https://user-images.githubusercontent.com/20828492/77301470-13225c00-6d33-11ea-814f-7e4d129d2891.png" width="200" height="200">

### 팀페이지 주소
https://kookmin-sw.github.io/capstone-2020-9/


## 1. 프로젝트 소개
화면을 보여주는 모니터, 빔 프로젝터 등은 컴퓨터에 있어서 가장 기본적인 출력장치들 중 하나이다. 시각적인 출력장치도 매우 많은 종류가 생기고 터치가 가능해지는 모니터, 터치를 인식하는 빔 프로젝터 등 출력장치에 입력장치를 추가하여 사용자와 상호작용을 하는 장치들 역시 매우 많이 개발되며 상용화 되고 있다. 기존의 제품을 사용해 입력장치를 추가하려면 추가적인 기기를 구매하거나 새로운 기기를 구매해야 한다.  
따라서 본 프로젝트는 빔 프로젝터, 모니터 등 PC와 연결된 스크린에서의 조작을 모바일에서의 모션인식을 통해 가능하게 하는 것을 목표로 한다.  대부분의 강의를 위한 공간은 빔 프로젝터나 큰 모니터를 사용한다. 하지만 발표를 하면서 동적인 자세 또는 특정 행동을 통해서 발표 스크린을 제어하는 것은 어렵다. 이 프로젝트는 모바일 디바이스에서의 모션 인식 기능을 통해 추가적인 비용 소모 없이 화면을 터치하는것과 같은 효과를 줄 것이다.  
사람의 손을 추적하고 손의 모션을 인식하는 딥러닝 모델을 학습하고, 해당 모델로 발표를 진행하는 사용자의 모션을 실시간으로 분석하여 화면을 제어하는 프로그램 개발을 목표로 한다. 모바일 어플리케이션에서 화면을 인식함으로 출력 장치에 무관한 화면 제어를 가능하게 한다.  

## 2. Abstract
Monitors, beam projectors and etc that show a screen are one of the most basic output devices in a computer. There are a lot of kinds of optical output devices. And also devices are being developed and commercialized by adding input stream in output devices, such as monitors and beam projectors that recognize touch. However, to add input function using existing products, you must purchase additional devices or purchase new devices  
Therefore, this project aims to enable control on screens connected to PCs such as beam projectors or monitors through motion recognition on mobile devices. Most of the spaces for lectures use a beam projector or a large monitor. However, it is difficult to control the presentation screen through dynamic guestures or specific actions during the presentation. So, this project will give the similar effect as touching the screen without additional cost through motion recognition on a mobile device.  
The goal is to develop a deep learning model that recognizes motion of hand and a program that control the screen by analyzes the motion of the user who conducts the presentation in real-time. So, screen recognition in mobile applications enables screen control regardless of the output device.  


## 3. 소개 영상
 - link
[![Watch the video](https://img.youtube.com/vi/FLWs8H1VLTo/maxresdefault.jpg)](https://youtu.be/FLWs8H1VLTo)

https://youtu.be/FLWs8H1VLTo

### 프로젝트 시나리오 영상
[![Watch the video](https://img.youtube.com/vi/Y0ykC2mQFJw/mqdefault.jpg)](https://youtu.be/Y0ykC2mQFJw)
https://youtu.be/Y0ykC2mQFJw

### 프로젝트 구현 영상
[![Watch the video](https://img.youtube.com/vi/w3RBwXQyVUQ/mqdefault.jpg)](https://youtu.be/w3RBwXQyVUQ)
https://youtu.be/w3RBwXQyVUQ

## 4. 실행 방법


1) server
    `running on aws`  
    `ip : 3.226.243.223`  
    `port : 8081`  
     
2) android app
    `src\tos_proto.apk 를 다운받아서 android 환경에 설치하고 실행한다.`  
3) window app
    `src\window_app\main.py 파일을 실행 시킨다`  
    `필요 python module : "pyautogui", "PyQt5", "win10toast"`  
    `실행 : user>python3 main.py`  
    
    

### 4.1 mediapipe 설치방법 (Linux)

https://mediapipe.readthedocs.io/en/latest/install.html#installing-on-debian-and-ubuntu

1)terminal에서 mediapipe의 깃 파일들을 불러온다.

    '$ git clone https://github.com/google/mediapipe.git'
    '$ cd mediapipe'
    
2)Bazel을 설치한다.

    https://docs.bazel.build/versions/master/install-ubuntu.html
    
3)OpenCV와 FFmpeg를 설치한다.

   opt1) pakage manager tool을 사용해 pre-compile 되어있는 OpenCV 라이브러리를 설치한다.
    
        '$ sudo apt-get install libopencv-core-dev libopencv-highgui-dev \
                       libopencv-calib3d-dev libopencv-features2d-dev \
                       libopencv-imgproc-dev libopencv-video-dev'
                       
   opt2) setup_opencv.sh를 실행하여 OpenCV를 자동 빌드한다. 그리고 Mediapipe의 OpenCV 구성을 수정한다.
    
   opt3) https://docs.opencv.org/3.4.6/d7/d9f/tutorial_linux_install.html (OpenCV manual)을 따라한다.
    
    추가. Mediapipe가 당신 컴퓨터의 OpenCV의 라이브러리를 가리키도록 WORKSPACE 와 opencv_linux.BUILD 를 수정해야한다.  만약 OpenCV4가 "/usr/local"에 설치되어있다면 당신은 WORKSPACE 의 "linux_opencv"new_local_repository rule과 opencv_linux.BUILD 의 "opencv" cc_library ruls을 수정해야한다.
    
    new_local_repository(
    name = "linux_opencv",
    build_file = "@//third_party:opencv_linux.BUILD",
    path = "/usr/local",
    )

    cc_library(
        name = "opencv",
        srcs = glob(
            [
                "lib/libopencv_core.so",
                "lib/libopencv_highgui.so",
                "lib/libopencv_imgcodecs.so",
                "lib/libopencv_imgproc.so",
                "lib/libopencv_video.so",
                "lib/libopencv_videoio.so",
            ],
        ),
        hdrs = glob(["include/opencv4/**/*.h*"]),
        includes = ["include/opencv4/"],
        linkstatic = 1,
        visibility = ["//visibility:public"],
    )
    
4)Hello World desktop example 실행
    
    $ export GLOG_logtostderr=1

    # if you are running on Linux desktop with CPU only
    $ bazel run --define MEDIAPIPE_DISABLE_GPU=1 \
        mediapipe/examples/desktop/hello_world:hello_world

    # If you are running on Linux desktop with GPU support enabled (via mesa drivers)
    $ bazel run --copt -DMESA_EGL_NO_X11_HEADERS --copt -DEGL_NO_X11 \
        mediapipe/examples/desktop/hello_world:hello_world

    # Should print:
    # Hello World!
    # Hello World!
    # Hello World!
    # Hello World!
    # Hello World!
    # Hello World!
    # Hello World!
    # Hello World!
    # Hello World!
    # Hello World!

### 4.2 python module 설치방법 
`pip install pyautogui`
`pip install PyQt5`
`pip install win10toast`




## 5. 팀 소개
<img src="https://user-images.githubusercontent.com/20828492/77650602-145fbd00-6faf-11ea-9f68-ad54d178baa1.jpg" width="160" height="200">

    - 정형섭(팀장)
      20153229
      bluesky096049@gmail.com
      모션인식 및 안드로이드 어플리케이션 연동


 
<img src="https://user-images.githubusercontent.com/20828492/77652703-03fd1180-6fb2-11ea-95b8-fe1abd59f51f.jpg" width="160" height="200">

    - 심유정
      20153192
      beanwolf@kookmin.ac.kr
      모션인식, github관리 
   
<img src="https://user-images.githubusercontent.com/20828492/77650563-05790a80-6faf-11ea-8932-84a15f540551.jpg" width="256" height="256">

    - 유성훈
      20153199
      ysg03004@kookmin.ac.kr
      모바일 프로그래밍 및 UI 
   
      
<img src="https://user-images.githubusercontent.com/20828492/77650650-24779c80-6faf-11ea-9e66-68652b3252ef.jpg" width="150" height="200">

    - 이규한
      20153206
      qr96@kookmin.ac.kr
      모바일 프로그래밍 
   
   
<img src="https://user-images.githubusercontent.com/20828492/77650692-335e4f00-6faf-11ea-842a-1aafd720f60c.jpg" width="160" height="200">

    - 조정근
      20153235
      barunpuri@kookmin.ac.kr
      PC 프로그래밍 및 서버 구현 
      


<!--
## Markdown을 사용하여 내용꾸미기

Markdown은 작문을 스타일링하기위한 가볍고 사용하기 쉬운 구문입니다. 여기에는 다음을위한 규칙이 포함됩니다.

```markdown
Syntax highlighted code block

# Header 1
## Header 2
### Header 3

- Bulleted
- List

1. Numbered
2. List

**Bold** and _Italic_ and `Code` text

[Link](url) and ![Image](src)
```

자세한 내용은 [GitHub Flavored Markdown](https://guides.github.com/features/mastering-markdown/).

### Support or Contact

readme 파일 생성에 추가적인 도움이 필요하면 [도움말](https://help.github.com/articles/about-readmes/) 이나 [contact support](https://github.com/contact) 을 이용하세요.
-->
