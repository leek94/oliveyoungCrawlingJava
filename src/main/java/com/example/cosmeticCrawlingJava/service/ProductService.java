package com.example.cosmeticCrawlingJava.service;

import com.example.cosmeticCrawlingJava.repository.CcTempProductRepository;
import com.example.cosmeticCrawlingJava.repository.ProductRepository;
import entity.CcTempProduct;
import entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ProductService {
    private static final String IMAGE_DIRECTORY = "/uploadc/contents/image/";

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CcTempProductRepository ccTempProductRepository;

    private Logger logger = Logger.getLogger(ProductService.class.getName());

    @Transactional
    public void processProducts(List<Product> productList, int productCount) {
        for (Product product : productList) {
            try{
                CcTempProduct ccTempProduct = new CcTempProduct();
                ccTempProduct.setProdCode(product.getProdCode());
                ccTempProduct.setSiteType(product.getSiteType());
                ccTempProductRepository.save(ccTempProduct);


                Product foundProduct = productRepository.findByComplexAttributes(
                        product.getProdCode(),
                        product.getSiteType(),
                        product.getSiteDepth1(),
                        product.getSiteDepth2(),
                        product.getSiteDepth3()
                );
                //난수 발생
                long randomNum = (long) (Math.random() * (10000000000L - 1000000000L) + 1000000000L);
                String randomNumber = Long.toString(randomNum);

                //현재 날짜 형식 변경
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
                String formattedDateTime = now.format(formatter);

                if (productCount == 0 || foundProduct == null) {
                    if (foundProduct.getPrice() > 0) {
                        String filePath = downloadImage(foundProduct);
                        product.setImg(filePath);
                        productRepository.save(product);



                    }
                }


            }catch (DataIntegrityViolationException e){
                if(e.getMessage().contains("Duplicate entry")){
                    logger.warning("Integrity error occurred: Duplicate entry");
                }
            }
        }
    }

    public String downloadImage(Product product) {
        String fileDirectory = IMAGE_DIRECTORY + product.getSiteType() + "/";
        String filePath = fileDirectory + product.getProdCode() + ".png";

        try{
            URL url = new URL(product.getImgPath());
            InputStream inputStream = url.openStream();

            Path path = Paths.get(fileDirectory);
            Files.createDirectories(path);

            if (!Files.exists(Paths.get(filePath))) {
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);

                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                fileOutputStream.close();
            }

            inputStream.close();
        } catch (IOException e){
            e.printStackTrace();

        }
        return filePath;
    }
}
