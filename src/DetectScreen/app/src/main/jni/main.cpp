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
                jintArray boundaries){

                Mat &matInput = *(Mat *)matAddrInput;
                Mat &matResult = *(Mat *)matAddrResult;

                Mat gray;
                Mat canny;

                vector<Vec4i> lines;

                cvtColor(matInput, gray, COLOR_BGR2GRAY);
                GaussianBlur(gray, gray, Size(9,9), 0);
                //threshold(gray, matInput, 125, 255, THRESH_BINARY);
                Canny(gray, canny, 120, 150, 3);
                HoughLinesP(canny, lines, 1, CV_PI/180, 20, 120, 5);//점 두개씩 들어감


                // 허프만에 찍힌  점들을 최소 제곱법을 이용해 직선 구해보자.
                int n;// 표본의 갯수

                vector<Vec4i> line1,line2,line3,line4;//상하좌우

                for( int i=0; i<lines.size(); i++ )// 위치에 따른 직선 분류
                {
                    Vec4i L = lines[i];
                    if(L[2]-L[0] != 0 && 3*abs(L[3]-L[1]) < abs(L[2]-L[0])){// 가로선, 기울기가 1/2보다 작은 경우
                        if(L[1]<300){
                            line1.push_back(L);
                        }
                        else if(L[1]>660){
                            line2.push_back(L);
                        }
                    }
                    else if (abs(L[3]-L[1]) > 4*abs(L[2]-L[0])){ // 세로선, 기울기가 2보다 큰 경우
                        if(L[0]<600){
                            line3.push_back(L);
                        }
                        else if (L[0]>1300){
                            line4.push_back(L);
                        }
                    }
                }

                double SigA;
                double SigB;
                double tmpA;
                double a1, b1;
                double a2, b2;
                double a3, b3;
                double a4, b4;
                int c3=-1;
                int c4=-1;

                //가로줄1
                SigA=0; SigB=0;
                n=line1.size();
                for(int i=0;i<n;i++){
                    Vec4d L = line1[i];
                    SigA+=(L[3]-L[1])/(L[2]-L[0]);
                    SigB+=L[1]-L[0]*(L[3]-L[1])/(L[2]-L[0]);
                }
                if(n!=0){
                    a1=SigA/n;
                    b1=SigB/n;
                }
                else{
                    a1=0;
                    b1=0;
                }

                //line(matInput, Point(0,b1), Point(2000,2000*a1+b1), Scalar(0,0,255), 5, LINE_AA );// 가로줄 출력

                //가로줄2
                SigA=0; SigB=0;
                n=line2.size();
                for(int i=0;i<n;i++){
                    Vec4d L = line2[i];
                    SigA+=(L[3]-L[1])/(L[2]-L[0]);
                    SigB+=L[1]-L[0]*(L[3]-L[1])/(L[2]-L[0]);
                }
                if(n!=0){
                    a2=SigA/n;
                    b2=SigB/n;
                }
                else{
                    a2=0;
                    b2=1070;
                }
                //line(matInput, Point(0,b2), Point(2000,2000*a2+b2), Scalar(0,255,0), 5, LINE_AA );// 가로줄 출력

                //세로줄1
                SigA=0; SigB=0;
                n=line3.size();
                for(int i=0;i<n;i++){
                    Vec4d L = line3[i];
                    SigA+=(L[3]-L[1])/(L[2]-L[0]);
                    SigB+=L[1]-L[0]*(L[3]-L[1])/(L[2]-L[0]);
                }
                if(n!=0 && SigA!=0){
                    a3=SigA/n;
                    b3=SigB/n;
                    //line(matInput, Point(-b3/a3,0), Point((1000-b3)/a3,1000), Scalar(0,0,255), 5, LINE_AA );// 세로줄1 출력
                }
                else{
                    c3=1;
                    //line(matInput, Point(c3,0), Point(c3,1000), Scalar(0,0,255), 5, LINE_AA );// 세로줄1 출력
                }

                //세로줄2
                SigA=0; SigB=0;
                n=line4.size();
                for(int i=0;i<n;i++){
                    Vec4d L = line4[i];
                    SigA+=(L[3]-L[1])/(L[2]-L[0]);
                    SigB+=L[1]-L[0]*(L[3]-L[1])/(L[2]-L[0]);
                }
                if(n!=0 && SigA!=0){
                    a4=SigA/n;
                    b4=SigB/n;
                    //line(matInput, Point(-b4/a4,0), Point((1000-b4)/a4,1000), Scalar(0,255,0), 5, LINE_AA );// 세로줄2 출력
                }
                else{
                    c4=1900;
                    //line(matInput, Point(c4,0), Point(c4,1000), Scalar(0,255,0), 5, LINE_AA );// 세로줄1 출력
                }

                Vec8i P;
                if(c3==-1 && c4==-1){
                    P[0]=(b3-b1)/(a1-a3);
                    P[1]=a1*P[0]+b1;
                    P[2]=(b4-b1)/(a1-a4);
                    P[3]=a1*P[2]+b1;
                    P[4]=(b4-b2)/(a2-a4);
                    P[5]=a2*P[4]+b2;
                    P[6]=(b3-b2)/(a2-a3);
                    P[7]=a2*P[6]+b2;
                }
                else if(c3==-1){
                    P[0]=(b3-b1)/(a1-a3);
                    P[1]=a1*P[0]+b1;
                    P[2]=c4;
                    P[3]=a1*P[2]+b1;
                    P[4]=c4;
                    P[5]=a2*P[4]+b2;
                    P[6]=(b3-b2)/(a2-a3);
                    P[7]=a2*P[6]+b2;
                }
                else if(c4==-1){
                    P[0]=c3;
                    P[1]=a1*P[0]+b1;
                    P[2]=(b4-b1)/(a1-a4);
                    P[3]=a1*P[2]+b1;
                    P[4]=(b4-b2)/(a2-a4);
                    P[5]=a2*P[4]+b2;
                    P[6]=c3;
                    P[7]=a2*P[6]+b2;
                }
                /*
                line(matInput, Point(P[0],P[1]), Point(P[2],P[3]), Scalar(255,0,0), 5, LINE_AA );
                line(matInput, Point(P[2],P[3]), Point(P[4],P[5]), Scalar(255,0,0), 5, LINE_AA );
                line(matInput, Point(P[4],P[5]), Point(P[6],P[7]), Scalar(255,0,0), 5, LINE_AA );
                line(matInput, Point(P[6],P[7]), Point(P[0],P[1]), Scalar(255,0,0), 5, LINE_AA );
                */
                jint *B;
                B = env->GetIntArrayElements(boundaries, NULL);
                for(int i=0;i<8;i++){
                    if(P[i]!=0) B[i] = (B[i]*8+P[i]*2)/10; // 기존값 8, 신규값 2의 비율로 적용
                }

                line(matInput, Point(B[0],B[1]), Point(B[2],B[3]), Scalar(255,0,0), 5, LINE_AA );
                line(matInput, Point(B[2],B[3]), Point(B[4],B[5]), Scalar(255,0,0), 5, LINE_AA );
                line(matInput, Point(B[4],B[5]), Point(B[6],B[7]), Scalar(255,0,0), 5, LINE_AA );
                line(matInput, Point(B[6],B[7]), Point(B[0],B[1]), Scalar(255,0,0), 5, LINE_AA );

                env->ReleaseIntArrayElements(boundaries, B, 0);
            }
}

