#include <jni.h>
#include "com_example_detectscreen_MainActivity.h"

#include <opencv2/opencv.hpp>

using namespace cv;
using namespace std;



extern "C" {
            JNIEXPORT void JNICALL Java_com_example_detectscreen_MainActivity_ConvertRGBtoGray
            (
                JNIEnv *env,
                jobject instance,
                jlong matAddrInput,
                jlong matAddrResult,
                jdouble ScreenSize){

                Mat &matInput = *(Mat *)matAddrInput;
                Mat &matResult = *(Mat *)matAddrResult;

                Mat gray;
                Mat canny;

                vector<Vec4i> lines;
                //vector<vector<Point>> contours;
                //vector<Point2f> approx;

                double leftDis=8000;
                double rightDis=-8000;
                double topDis=-8000;
                double bottomDis=8000;
                //y=mx+b 형태, c는 x=N 나타내기 위해
                double m1,b1,c1;//좌
                double m2,b2,c2;//우
                double m3,b3;//상
                double m4,b4;//하

                cvtColor(matInput, gray, COLOR_BGR2GRAY);
                GaussianBlur(gray, gray, Size(9,9), 0);
                //threshold(gray, matInput, 125, 255, THRESH_BINARY);
                Canny(gray, canny, 120, 150, 3);

                HoughLinesP(canny, lines, 1, CV_PI/180, 20, 120, 3);//점 두개씩 들어감

                for( int i=0; i<lines.size(); i++ )
                {
                    Vec4i L = lines[i];
                    double m;//기울기
                    double xSlice, ySlice;//x,y절편

                    if(L[2]-L[0] != 0){//가장 끝 직선 구하는 부분
                        m=(double)(L[3]-L[1])/(double)(L[2]-L[0]);//기울기 구함
                        ySlice = (double)L[1] - (double)L[0]*m;

                        if(fabs(m)>1.2f){ //기울기가 1.2가 넘는 경우 세로줄로 판단
                            xSlice = (double)L[0] - ((double)L[1]/m);
                            if(xSlice < leftDis){ // 해당 직선이 더 왼쪽인 경우
                                leftDis = xSlice;
                                m1=m;//해당 직선 추가
                                b1=ySlice;
                            }
                            if(xSlice > rightDis){ // 해당 직선이 더 오른쪽인 경우
                                rightDis = xSlice;
                                m2=m;
                                b2=ySlice;
                            }
                        }
                        else if (fabs(m)<1.0f){//기울기가 1이 안되면 가로줄로 판단
                            if(ySlice > topDis){// 더 위에 있는 경우
                                topDis = ySlice;
                                m3 = m;
                                b3 = ySlice;
                            }
                            if(ySlice < bottomDis){// 더 아래 있는 경우
                                bottomDis = ySlice;
                                m4 = m;
                                b3 = ySlice;
                            }
                        }
                    }
                    else {// 기울기의 분자 0인 경우, 무조건 세로줄
                        if(L[0]<leftDis){// 더 왼쪽인 경우
                            leftDis=L[0];
                            c1=L[0];
                        }
                        if(L[0]>rightDis){// 더 오른쪽인 경우
                            rightDis=L[0];
                            c2=L[0];
                        }
                    }

                    line(matInput, Point(L[0],L[1]), Point(L[2],L[3]),
                         Scalar(0,0,255), 5, LINE_AA );
                }

                // 직선의 교점 찾아 스크린의  끝 점 구해야함

                /*칸투어 방식
                findContours(canny, contours, RETR_LIST, CHAIN_APPROX_NONE);

                for(int i=0;i<4;i++){// 면적이 넓은 칸투어 찾는다
                    for(int j=i+1;j<contours.size();j++){
                        if(fabs(contourArea(Mat(contours[i])))<fabs(contourArea(Mat(contours[j])))){
                            swap(contours[i],contours[j]);
                        }
                    }
                }


                for(int i=0;i<6;i++){//근사하여 꼭짓점 4개인 도형 찾음
                    approxPolyDP(Mat(contours[i]), approx, arcLength(Mat(contours[i]), true)*0.02, true);//2퍼센트 오차, 도형 근사화

                    if(approx.size() == 4) break;

                }

                if(fabs(contourArea(Mat(approx))) > ScreenSize-300){//이전 도형과 크기 비슷하면
                    for (int k = 0; k < approx.size() - 1; k++)//직선 그린다
                        line(matInput, approx[k], approx[k + 1], Scalar(0, 255, 0), 3);
                    line(matInput, approx[0], approx[approx.size() - 1], Scalar(0, 255, 0), 3);
                    ScreenSize = fabs(contourArea(Mat(approx)));
                }
                */
            }
}

