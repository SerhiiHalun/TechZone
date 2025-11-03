package com.example.techzone.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig{
        @Bean
        public Cloudinary cloudinary() {
            return new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "CLOUDINARY_YOUR_CLOUD_NAME",
                    "api_key", "CLOUDINARY_YOUR_API_KEY",
                    "api_secret", "CLOUDINARY_YOUR_API_SECRET",
                    "secure", true
            ));
        }
}