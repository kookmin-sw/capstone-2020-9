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
                jlong matAddrResult){

                Mat &matInput = *(Mat *)matAddrInput;
                Mat &matResult = *(Mat *)matAddrResult;

                Mat gray;
                Mat canny;
                vector<vector<Point>> contours;
                vector<Point2f> approx;

                cvtColor(matInput, gray, COLOR_BGR2GRAY);
                GaussianBlur(gray, gray, Size(5,5), 0);
                Canny(gray, canny, 95, 200);
                findContours(canny, contours, RETR_LIST, CHAIN_APPROX_SIMPLE);

                for(int i=0;i<contours.size();i++){
                    approxPolyDP(Mat(contours[i]), approx, arcLength(Mat(contours[i]), true)*0.02, true);//2퍼센트 오차, 도형 근사화

                    if(fabs(contourArea(Mat(approx))) > 2500){//일정 크기 이상만
                            line(matInput, approx[0], approx[approx.size() - 1], Scalar(0, 255, 0), 3);

                            for (int k = 0; k < approx.size() - 1; k++)
                                line(matInput, approx[k], approx[k + 1], Scalar(0, 255, 0), 3);
                    }

                }
            }
}
