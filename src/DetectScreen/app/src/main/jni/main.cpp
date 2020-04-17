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


                cvtColor(matInput, gray, COLOR_BGR2GRAY);
                GaussianBlur(gray, gray, Size(9,9), 0);
                //threshold(gray, matInput, 125, 255, THRESH_BINARY);
                Canny(gray, canny, 120, 150, 3);

                HoughLinesP(canny, lines, 1, CV_PI/180, 20, 120, 5);//점 두개씩 들어감


                /*
                for( int i=0; i<lines.size(); i++ )
                {
                    Vec4i L = lines[i];

                    line(matInput, Point(L[0],L[1]), Point(L[2],L[3]), Scalar(0,0,255), 5, LINE_AA );
                }*/


                // 허프만에 찍힌  점들을 최소 제곱법을 이용해 직선 구해보자.
                double a, b, c; // y = ax + b or x=c
                int SigXY=0;
                int SigX=0;
                int SigY=0;
                int SigX2=0;//X의 제곱
                int SigY2=0;//y의 제곱
                int n=lines.size()*2;// 표본의 갯수

                vector<Vec4i> line1,line2,line3,line4;

                for( int i=0; i<lines.size(); i++ )
                {
                    Vec4i L = lines[i];
                    if(L[2]-L[0] != 0 && 2*abs(L[3]-L[1]) < abs(L[2]-L[0])){// 가로선, 기울기가 1/2보다 작은 경우
                        if(L[1]<333){
                            line1.push_back(L);
                        }
                        else if(L[1]>700){
                            line2.push_back(L);
                        }
                    }
                    else if (abs(L[3]-L[1]) > 2*abs(L[2]-L[0])){ // 세로선, 기울기가 2보다 큰 경우
                        if(L[0]<666){
                            line3.push_back(L);
                        }
                        else if (L[0]>1333){
                            line4.push_back(L);
                        }
                    }
                }


                for(int i=0;i<line2.size();i++){
                    Vec4i L = line2[i];
                    //line(matInput, Point(L[0],L[1]), Point(L[2],L[3]), Scalar(0,0,255), 5, LINE_AA );
                    SigXY+=L[0]*L[1]+L[2]*L[3];
                    SigX+=L[0]+L[2];
                    SigY+=L[1]+L[3];
                    SigX2+=L[0]*L[0]+L[2]*L[2];
                    SigY2+=L[1]*L[1]+L[3]*L[3];

                    line(matInput, Point(L[0],L[1]), Point(L[2],L[3]), Scalar(0,0,255), 5, LINE_AA );
                }
                n=line2.size()*2;

                a = (double)(n*SigXY-SigX*SigY)/(double)(n*SigX2-SigX*SigX);
                b = (double)(SigX2*SigY-SigX*SigXY)/(double)(n*SigX2-SigX*SigX);

                line(matInput, Point(0,b), Point(2000,2000*a+b), Scalar(0,0,255), 5, LINE_AA );
                //line(matInput, Point(-b/a,0), Point((1000-b)/a,1000), Scalar(0,0,255), 5, LINE_AA );


                /* 최소제곱법 공식 위해
                SigXY+=L[0]*L[1]+L[2]*L[3];
                SigX+=L[0]+L[2];
                SigY+=L[1]+L[3];
                SigX2+=L[0]*L[0]+L[2]*L[2];
                SigY2+=L[1]*L[1]+L[3]*L[3];
                */


                /*
                int x1,y1, x2,y2;

                if(c1==0){ // 직선들의 교점 구하여 스크린 끝점 구하는 부분
                    x1 = (b3-b1)/(m1-m3);
                    y1 = m3*x1+b3;
                    x2 = (b4-b1)/(m1-m4);
                    y2 = m4*x2+b4;
                }
                else{ // x=N 직선, 세로줄
                    x1 = c1;
                    y1 = m3*x1+b3;
                    x2 = c1;
                    y2 = m4*x2+b4;
                }
                points.push_back(Vec2i(x1,y1));
                points.push_back(Vec2i(x2,y2));
                if(c2==0){
                    x1 = (b3-b2)/(m2-m3);
                    y1 = m2*x2+b2;
                    x2 = (b4-b2)/(m2-m4);
                    y2 = m2*x2+b2;
                }
                else{ // x=N 직선, 세로줄
                    x1 = c2;
                    y1 = m3*x1+b3;
                    x2 = c2;
                    y2 = m4*x2+b4;
                }
                points.push_back(Vec2i(x1,y1));
                points.push_back(Vec2i(x2,y2));

                for(int i=0;i<points.size();i++){
                    Vec2i L1 = points[i];
                    Vec2i L2 = points[++i];
                    line(matInput, Point(L1[0],L1[1]), Point(L2[0],L2[1]),
                         Scalar(0,0,255), 5, LINE_AA );
                }
                */

                /*칸투어 방식 , 사각형 꼭짓점 야무지게 찾아내지만 꼭짓점  하나라도 가려지면 못찾는다
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

