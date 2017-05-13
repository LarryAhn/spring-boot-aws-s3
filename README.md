# Spring Boot AWS S3

```bash
// git clone
$ git clone https://github.com/LawrenceAhn/spring-boot-aws-s3 && cd spring-boot-aws-s3
$ mvn install
```

```xml
<!-- AWS S3 -->
<!-- Github Repo : https://github.com/LawrenceAhn/spring-boot-aws-s3 -->
<dependency>
    <groupId>com.zuperztarahn</groupId>
    <artifactId>spring-boot-starter-aws-s3</artifactId>
    <version>0.0.2</version>
</dependency>
```

```yaml
# AWS
amazon:
  aws:
    access-key-id: {access-key-id}
    access-key-secret: {access-key-secret}
  s3:
    default-bucket: {bucket-name}
    prefix-url: https://s3-ap-northeast-1.amazonaws.com/
```

```java

{...}

@Value("${amazon.s3.prefix-url}")

private String prefixUrl;

private AmazonS3Template amazonS3Template;

private String bucketName;

@Autowired
@SuppressWarnings("SpringJavaAutowiringInspection")
public UserRestController(AmazonS3Template amazonS3Template, 
                          @Value("${amazon.s3.default-bucket}") String bucketName) {
    this.amazonS3Template = amazonS3Template;
    this.bucketName = bucketName;
}

@PostMapping("/users/profilePhoto")
@ApiImplicitParam(name = "Authorization", value = "토큰", dataType = "String", paramType = "header", required = true)
@ApiOperation(value = "사용자 프로필 사진 업로드 API",
        notes = "사용자 프로필 사진 업로드 API", tags = {"1.1 사용자 프로필 사진 업로드 API"})
public ResponseEntity<?> userProfilePhotoUpload(@RequestParam("file") MultipartFile file) {

    String uploadFileName = UUID.randomUUID().toString();

    if (!file.isEmpty()) {
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());

            byte[] bytes = IOUtils.toByteArray(file.getInputStream());
            objectMetadata.setContentLength(bytes.length);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            amazonS3Template.getAmazonS3Client().putObject(new PutObjectRequest(bucketName + "/profile", uploadFileName, byteArrayInputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            // userService.updateProfilePhoto(prefixUrl + bucketName + "/profile/" + uploadFileName);
        } catch (Exception e) {
            throw new ExampleApiException(e.getMessage());
        }
    } else {
        throw new ExampleApiException("등록할 프로필 사진이 없습니다.");
    }
    ExampleApiResponse result = new ExampleApiResponse();
    result.buildSuccess(HttpStatus.OK.value(), "success", prefixUrl + bucketName + "/profile/" + uploadFileName);
    return new ResponseEntity<>(result, HttpStatus.OK);
}

{...}

```

