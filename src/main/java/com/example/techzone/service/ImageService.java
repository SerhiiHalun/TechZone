package com.example.techzone.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import com.example.techzone.model.Image;
import com.example.techzone.model.Product;
import com.example.techzone.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class ImageService {
    private final Cloudinary cloudinary;
    private final ImageRepository imageRepository;
    @Autowired
    public ImageService(Cloudinary cloudinary, ImageRepository imageRepository) {
        this.cloudinary = cloudinary;
        this.imageRepository = imageRepository;
    }


    @Transactional
    public Image addImageToProduct(MultipartFile file, Product product, boolean isMain) {

        String publicId;
        String secureUrl;
        try {
            Map uploadResult = cloudinary.uploader()
                    .upload(file.getBytes(), ObjectUtils.emptyMap());
            secureUrl = (String) uploadResult.get("secure_url");
            publicId = (String) uploadResult.get("public_id");
        } catch (IOException e) {
            throw new RuntimeException("Error uploading to Cloudinary", e);
        }


        if (isMain) {
            imageRepository.updateIsMainFalseForProduct(product.getId());
        }


        Image image = new Image();
        image.setImgUrl(secureUrl);
        image.setPublicId(publicId);
        image.setMain(isMain);
        image.setProduct(product);


        return imageRepository.save(image);
    }

    @Transactional(readOnly = true)
    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Image with id " + id + " not found"));
    }
    @Transactional(readOnly = true)
    public List<Image> getImagesByProductId(int productId) {
        return imageRepository.findAllByProduct_Id(productId);
    }

    @Transactional(readOnly = true)
    public List<Image> getAllMainImages() {
        return imageRepository.findAllByIsMainTrue();
    }

    @Transactional(readOnly = true)
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    @Transactional
    public Image updateImage(Long imageId, MultipartFile newFile, Boolean newIsMain) {
        Image image = getImageById(imageId);


        if (newFile != null && !newFile.isEmpty()) {

            if (image.getPublicId() != null) {
                try {
                    cloudinary.uploader().destroy(image.getPublicId(), ObjectUtils.emptyMap());
                } catch (IOException e) {

                }
            }

            try {
                Map uploadResult = cloudinary.uploader().upload(newFile.getBytes(), ObjectUtils.emptyMap());
                image.setImgUrl((String) uploadResult.get("secure_url"));
                image.setPublicId((String) uploadResult.get("public_id"));
            } catch (IOException e) {
                throw new RuntimeException("Error uploading new file to Cloudinary", e);
            }
        }

        if (newIsMain != null) {
            if (newIsMain) {
                imageRepository.updateIsMainFalseForProduct(image.getProduct().getId());
            }
            image.setMain(newIsMain);
        }

        return imageRepository.save(image);
    }


    @Transactional
    public void deleteImage(Long imageId) {
        Image image = getImageById(imageId);

        if (image.getPublicId() != null) {
            try {
                cloudinary.uploader().destroy(image.getPublicId(), ObjectUtils.emptyMap());
            } catch (IOException e) {

            }
        }


        imageRepository.delete(image);
    }


    @Transactional
    public void setMainImage(long imageId) {
        Image image = getImageById(imageId);
        imageRepository.updateIsMainFalseForProduct(image.getProduct().getId());
        image.setMain(true);
        imageRepository.save(image);
    }
}


