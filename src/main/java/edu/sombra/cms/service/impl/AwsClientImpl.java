package edu.sombra.cms.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import edu.sombra.cms.messages.SomethingWentWrongException;
import edu.sombra.cms.service.AwsClient;
import edu.sombra.cms.util.LoggingService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static edu.sombra.cms.messages.StudentLessonMessage.NO_FILE_FOUND_WITH_SUCH_KEY;

@Service
public class AwsClientImpl implements AwsClient {

    private static final LoggingService LOGGER = new LoggingService(AwsClientImpl.class);

    private static final AWSCredentials credentials = new BasicAWSCredentials(
            "AKIA5EQS75LJ7HIS3BW5",
            "bvwMwj+GnLfFwhqmElqnVjsvCP9qG3RneQlZxzgK"
    );

    private static final AmazonS3 s3client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.EU_CENTRAL_1)
            .build();

    @Override
    public void upload(final String bucketName, final String key, final File file) throws SdkClientException {
        s3client.putObject(bucketName, key, file);
    }

    @Override
    public byte[] get(final String bucketName, final String key) throws SomethingWentWrongException {
        try {
            S3Object object = s3client.getObject(bucketName, key);

            return IOUtils.toByteArray(object.getObjectContent());
        } catch (AmazonServiceException e) {
            LOGGER.error("Unable to get file", e);
        } catch (FileNotFoundException e) {
            throw NO_FILE_FOUND_WITH_SUCH_KEY.ofException();
        } catch (IOException e) {
            LOGGER.error("Unable to process file", e);
        }

        throw NO_FILE_FOUND_WITH_SUCH_KEY.ofException();
    }

}
