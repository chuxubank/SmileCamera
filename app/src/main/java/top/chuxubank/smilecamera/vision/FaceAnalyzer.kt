package top.chuxubank.smilecamera.vision

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import top.chuxubank.smilecamera.utilities.OnDetectFaceListener

class FaceAnalyzer(private val listener: OnDetectFaceListener) : ImageAnalysis.Analyzer {

    private val faceDetectorOptions = FaceDetectorOptions.Builder()
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setContourMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setMinFaceSize(0.1F)
        .enableTracking()
        .build()

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val detector = FaceDetection.getClient(faceDetectorOptions)
            detector.process(image)
                .addOnSuccessListener {
                    if (it.size > 0 && it[0].smilingProbability!! > 0.5) {
                        listener()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("", "Face detection failed $e")
                }
                // When the image is from CameraX analysis use case, must call image.close() on received
                // images when finished using them. Otherwise, new images may not be received or the camera
                // may stall.
                .addOnCompleteListener { imageProxy.close() }
        }
    }
}