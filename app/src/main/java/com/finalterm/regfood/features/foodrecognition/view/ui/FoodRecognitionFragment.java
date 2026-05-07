package com.finalterm.regfood.features.foodrecognition.view.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.finalterm.regfood.R;
import com.finalterm.regfood.features.foodrecognition.data.model.FoodPredictionResponse;
import com.finalterm.regfood.features.foodrecognition.view.viewmodel.FoodRecognitionViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FoodRecognitionFragment extends Fragment {
    private static final String TAG = "FoodRecognition";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String IMAGE_DIR = "images";

    private LinearLayout cameraStateRoot;
    private LinearLayout loadingStateRoot;
    private LinearLayout resultStateRoot;
    private LinearLayout errorStateRoot;

    private FrameLayout cameraPreviewContainer;
    private Button btnGallery, btnCapture, btnFlash, btnClose;
    private Button btnCancelRecognition;
    private Button btnScanAgain, btnConfirmFood;
    private Button btnRetry;

    private ImageView resultImage;
    private TextView tvFoodName, tvConfidence;
    private TextView tvErrorMessage, tvLoadingStatus;
    private LinearLayout suggestionsContainer;
    private View lowConfidenceWarning;
    private TextView tvSuggestionsTitle;

    private FoodRecognitionViewModel viewModel;

    private File currentCapturedImageFile;
    private Uri currentImageUri;

    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;

    public FoodRecognitionFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_food_recognition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeUI(view);
        viewModel = new ViewModelProvider(this).get(FoodRecognitionViewModel.class);
        setupActivityResultLaunchers();
        setupListeners();
        observeViewModel();
        requestCameraPermissionIfNeeded();
        showCameraState();
    }

    private void initializeUI(View view) {
        cameraStateRoot = view.findViewById(R.id.cameraStateRoot);
        loadingStateRoot = view.findViewById(R.id.loadingStateRoot);
        resultStateRoot = view.findViewById(R.id.resultStateRoot);
        errorStateRoot = view.findViewById(R.id.errorStateRoot);

        cameraPreviewContainer = view.findViewById(R.id.cameraPreviewContainer);
        btnGallery = view.findViewById(R.id.btnGallery);
        btnCapture = view.findViewById(R.id.btnCapture);
        btnFlash = view.findViewById(R.id.btnFlash);
        btnClose = view.findViewById(R.id.btnClose);

        btnCancelRecognition = view.findViewById(R.id.btnCancelRecognition);
        tvLoadingStatus = view.findViewById(R.id.tvLoadingStatus);

        resultImage = view.findViewById(R.id.resultImage);
        tvFoodName = view.findViewById(R.id.tvFoodName);
        tvConfidence = view.findViewById(R.id.tvConfidence);
        lowConfidenceWarning = view.findViewById(R.id.lowConfidenceWarning);
        suggestionsContainer = view.findViewById(R.id.suggestionsContainer);
        tvSuggestionsTitle = view.findViewById(R.id.tvSuggestionsTitle);

        btnScanAgain = view.findViewById(R.id.btnScanAgain);
        btnConfirmFood = view.findViewById(R.id.btnConfirmFood);

        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);
        btnRetry = view.findViewById(R.id.btnRetry);
    }

    private void setupActivityResultLaunchers() {
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (!Boolean.TRUE.equals(granted)) {
                        Toast.makeText(requireContext(), "Quyền camera bị từ chối", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (Boolean.TRUE.equals(success) && currentImageUri != null && currentCapturedImageFile != null) {
                        if (!currentCapturedImageFile.exists() || currentCapturedImageFile.length() == 0L) {
                            File copiedFile = copyUriToCacheFile(currentImageUri);
                            if (copiedFile != null) {
                                currentCapturedImageFile = copiedFile;
                            }
                        }

                        if (currentCapturedImageFile != null && currentCapturedImageFile.exists()) {
                            handleCapturedImage(currentImageUri);
                        } else {
                            Toast.makeText(requireContext(), "Ảnh chụp không được lưu đúng cách", Toast.LENGTH_SHORT).show();
                            cleanupPendingImageFile();
                        }
                    } else {
                        cleanupPendingImageFile();
                    }
                }
        );

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        currentImageUri = uri;
                        processGalleryImage(uri);
                    }
                }
        );
    }

    private void setupListeners() {
        btnGallery.setOnClickListener(v -> openGallery());
        btnCapture.setOnClickListener(v -> capturePhoto());
        btnFlash.setOnClickListener(v -> toggleFlash());
        btnClose.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        btnCancelRecognition.setOnClickListener(v -> {
            viewModel.cancel();
            showCameraState();
        });

        btnScanAgain.setOnClickListener(v -> showCameraState());
        btnConfirmFood.setOnClickListener(v -> confirmFoodSelection());
        btnRetry.setOnClickListener(v -> retryRecognition());
    }

    private void observeViewModel() {
        viewModel.getPredictionResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                displayResult(result);
                showResultState();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                displayError(error);
                showErrorState();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading) {
                showLoadingState();
            }
        });

        viewModel.getStatusMessage().observe(getViewLifecycleOwner(), status -> {
            if (status != null) {
                tvLoadingStatus.setText(status);
            }
        });
    }

    private void requestCameraPermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openGallery() {
        pickImageLauncher.launch("image/*");
    }

    private void capturePhoto() {
        File imageFile = createImageFile();
        if (imageFile == null) {
            Toast.makeText(requireContext(), "Không thể tạo file ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        currentCapturedImageFile = imageFile;
        currentImageUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".fileprovider",
                imageFile
        );
        takePictureLauncher.launch(currentImageUri);
    }

    private File createImageFile() {
        try {
            File imageDir = new File(requireContext().getCacheDir(), IMAGE_DIR);
            if (!imageDir.exists() && !imageDir.mkdirs()) {
                Log.e(TAG, "Cannot create cache image directory");
                return null;
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            return new File(imageDir, "food_" + timestamp + ".jpg");
        } catch (Exception e) {
            Log.e(TAG, "Error creating image file", e);
            return null;
        }
    }

    private void handleCapturedImage(Uri imageUri) {
        try {
            Bitmap bitmap = decodeBitmapFromUri(imageUri);
            if (bitmap != null) {
                resultImage.setImageBitmap(bitmap);
                recognizeFood(currentCapturedImageFile);
            } else {
                Toast.makeText(requireContext(), "Không đọc được ảnh vừa chụp", Toast.LENGTH_SHORT).show();
                cleanupPendingImageFile();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling captured image", e);
            Toast.makeText(requireContext(), "Lỗi xử lý ảnh chụp", Toast.LENGTH_SHORT).show();
        }
    }

    private void processGalleryImage(Uri imageUri) {
        try {
            Bitmap bitmap = decodeBitmapFromUri(imageUri);
            if (bitmap == null) {
                Toast.makeText(requireContext(), "Không đọc được ảnh từ thư viện", Toast.LENGTH_SHORT).show();
                return;
            }

            currentCapturedImageFile = saveBitmapToCacheFile(bitmap);
            if (currentCapturedImageFile != null) {
                resultImage.setImageBitmap(bitmap);
                recognizeFood(currentCapturedImageFile);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing gallery image", e);
            Toast.makeText(requireContext(), "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap decodeBitmapFromUri(Uri uri) throws IOException {
        InputStream inputStream = requireActivity().getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            return null;
        }
        try {
            return BitmapFactory.decodeStream(inputStream);
        } finally {
            inputStream.close();
        }
    }

    private File saveBitmapToCacheFile(Bitmap bitmap) {
        File imageFile = createImageFile();
        if (imageFile == null) {
            return null;
        }

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, fos);
            fos.flush();
            Log.d(TAG, "Bitmap saved: " + imageFile.getAbsolutePath());
            return imageFile;
        } catch (Exception e) {
            Log.e(TAG, "Error saving bitmap", e);
            return null;
        }
    }

    private File copyUriToCacheFile(Uri sourceUri) {
        File targetFile = createImageFile();
        if (targetFile == null) {
            return null;
        }

        try (InputStream inputStream = requireActivity().getContentResolver().openInputStream(sourceUri);
             FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            if (inputStream == null) {
                return null;
            }

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            Log.d(TAG, "Copied captured image to cache: " + targetFile.getAbsolutePath());
            return targetFile;
        } catch (Exception e) {
            Log.e(TAG, "Error copying uri to cache", e);
            return null;
        }
    }

    private void cleanupPendingImageFile() {
        if (currentCapturedImageFile != null && currentCapturedImageFile.exists()) {
            // best-effort cleanup
            //noinspection ResultOfMethodCallIgnored
            currentCapturedImageFile.delete();
        }
        currentCapturedImageFile = null;
        currentImageUri = null;
    }

    private void recognizeFood(File imageFile) {
        if (imageFile == null || !imageFile.exists()) {
            Toast.makeText(requireContext(), "Không thể gửi ảnh vì file không tồn tại", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "recognizeFood called with missing file: " + imageFile);
            return;
        }
        viewModel.recognizeFood(imageFile);
    }

    private void toggleFlash() {
        Toast.makeText(requireContext(), "Flash toggle", Toast.LENGTH_SHORT).show();
    }

    private String getServerUrl() {
        return com.finalterm.regfood.features.foodrecognition.data.api.ServerConfig.BASE_URL
                + com.finalterm.regfood.features.foodrecognition.data.api.ServerConfig.RECOGNIZE_PATH;
    }

    private void displayResult(FoodPredictionResponse response) {
        if (response.data == null) {
            displayError("Phản hồi từ server không hợp lệ");
            return;
        }

        tvFoodName.setText(response.data.name != null ? response.data.name : response.data.food);
        tvConfidence.setText(String.format(Locale.US, "%.1f%%", response.data.confidence * 100));

        if (response.data.lowConfidence || response.data.isLowConfidence) {
            lowConfidenceWarning.setVisibility(View.VISIBLE);
        } else {
            lowConfidenceWarning.setVisibility(View.GONE);
        }

        suggestionsContainer.removeAllViews();
        if (response.data.suggestions != null && !response.data.suggestions.isEmpty()) {
            tvSuggestionsTitle.setVisibility(View.VISIBLE);
            for (FoodPredictionResponse.SuggestionItem suggestion : response.data.suggestions) {
                addSuggestionView(suggestion);
            }
        } else {
            tvSuggestionsTitle.setVisibility(View.GONE);
        }

        Log.d(TAG, "Result displayed: " + response.data.food);
    }

    private void addSuggestionView(FoodPredictionResponse.SuggestionItem suggestion) {
        androidx.cardview.widget.CardView card = new androidx.cardview.widget.CardView(requireContext());
        card.setCardElevation(2);
        card.setRadius(8);
        card.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        ((LinearLayout.LayoutParams) card.getLayoutParams()).bottomMargin = 8;

        LinearLayout cardContent = new LinearLayout(requireContext());
        cardContent.setOrientation(LinearLayout.HORIZONTAL);
        cardContent.setPadding(16, 12, 16, 12);

        TextView nameView = new TextView(requireContext());
        nameView.setText(suggestion.name);
        nameView.setTextSize(14);
        nameView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        ));

        TextView confidenceView = new TextView(requireContext());
        confidenceView.setText(String.format(Locale.US, "%.1f%%", suggestion.confidence * 100));
        confidenceView.setTextSize(12);
        confidenceView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray));

        cardContent.addView(nameView);
        cardContent.addView(confidenceView);
        card.addView(cardContent);

        suggestionsContainer.addView(card);
    }

    private void displayError(String message) {
        tvErrorMessage.setText(message);
        Log.e(TAG, "Error: " + message);
    }

    private void confirmFoodSelection() {
        FoodPredictionResponse result = viewModel.getPredictionResult().getValue();
        if (result != null && result.data != null) {
            Toast.makeText(requireContext(), "Đã chọn: " + result.data.name, Toast.LENGTH_SHORT).show();
        }
    }

    private void retryRecognition() {
        if (currentCapturedImageFile != null && currentCapturedImageFile.exists()) {
            recognizeFood(currentCapturedImageFile);
        } else {
            showCameraState();
        }
    }

    private void showCameraState() {
        cameraStateRoot.setVisibility(View.VISIBLE);
        loadingStateRoot.setVisibility(View.GONE);
        resultStateRoot.setVisibility(View.GONE);
        errorStateRoot.setVisibility(View.GONE);
    }

    private void showLoadingState() {
        cameraStateRoot.setVisibility(View.GONE);
        loadingStateRoot.setVisibility(View.VISIBLE);
        resultStateRoot.setVisibility(View.GONE);
        errorStateRoot.setVisibility(View.GONE);
    }

    private void showResultState() {
        cameraStateRoot.setVisibility(View.GONE);
        loadingStateRoot.setVisibility(View.GONE);
        resultStateRoot.setVisibility(View.VISIBLE);
        errorStateRoot.setVisibility(View.GONE);
    }

    private void showErrorState() {
        cameraStateRoot.setVisibility(View.GONE);
        loadingStateRoot.setVisibility(View.GONE);
        resultStateRoot.setVisibility(View.GONE);
        errorStateRoot.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.cancel();
    }
}
