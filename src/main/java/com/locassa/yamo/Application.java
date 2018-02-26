package com.locassa.yamo;

import com.locassa.yamo.model.*;
import com.locassa.yamo.model.enums.ContentType;
import com.locassa.yamo.model.enums.UserType;
import com.locassa.yamo.model.enums.VenueType;
import com.locassa.yamo.repository.*;
import com.locassa.yamo.service.aws.AwsS3Service;
import com.locassa.yamo.service.aws.AwsSNSService;
import com.locassa.yamo.util.YamoUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@SpringBootApplication
@EnableScheduling
public class Application {

    @Value("${yamo.admin.user.email}")
    private String adminEmail;

    @Value("${yamo.admin.user.password}")
    private String adminPassword;

    @Value("${aws.key}")
    private String key;

    @Value("${aws.secret}")
    private String secret;

    @Value("${aws.sns.endpoint}")
    private String snsEndpoint;

    @Value("${aws.s3.endpoint}")
    private String s3endpoint;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${mail.host}")
    private String mailHost;

    @Value("${mail.username}")
    private String mailUsername;

    @Value("${mail.password}")
    private String mailPassword;

    @Value("${mail.port}")
    private int mailPort;

    @Value("${mail.protocol}")
    private String mailProtocol;

    @Value("${mail.from}")
    private String from;

    @Value("${mail.auth}")
    private boolean mailAuth;

    @Value("${mail.ssl}")
    private boolean mailSsl;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(final UserRepository userRepository,
                           final VenueRepository venueRepository,
                           final MediumRepository mediumRepository,
                           final MovementRepository movementRepository,
                           final ContentRepository contentRepository) {

        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {

                // Save admin user if it does not exist.
                if (0 == userRepository.count()) {

                    User adminUser = new User();
                    adminUser.setFirstName("Yamo");
                    adminUser.setLastName("Admin User");
                    adminUser.setEmail(adminEmail);
                    adminUser.setPassword(new BCryptPasswordEncoder().encode(adminPassword));
                    adminUser.setSecretCode(null);
                    adminUser.setFacebookId(null);
                    // FIXME Image should be uploaded to AWS S3 service.
                    adminUser.setProfileImageUrl(YamoUtils.DEFAULT_PROFILE_IMAGE_PLACEHOLDER);

                    adminUser.setCity(YamoUtils.DEFAULT_CITY);
                    adminUser.setLat(YamoUtils.DEFAULT_LOCATION_LAT);
                    adminUser.setLon(YamoUtils.DEFAULT_LOCATION_LON);
                    adminUser.setLocation(YamoUtils.DEFAULT_CITY);

                    adminUser.setEnabled(true);
                    adminUser.setVisible(true);
                    adminUser.setNickname(null);
                    adminUser.setNickNameEnabled(false);
                    adminUser.setSignUpCompleted(true);
                    adminUser.setReady(true);
                    adminUser.setUserType(UserType.ADMIN);

                    userRepository.save(adminUser);

                    User oneUser = new User();
                    oneUser.setFirstName("One");
                    oneUser.setLastName("Test");
                    oneUser.setEmail("one@yamo.com");
                    oneUser.setPassword(new BCryptPasswordEncoder().encode(adminPassword));
                    oneUser.setSecretCode(null);
                    oneUser.setFacebookId(null);
                    // FIXME Image should be uploaded to AWS S3 service.
                    oneUser.setProfileImageUrl(YamoUtils.DEFAULT_PROFILE_IMAGE_PLACEHOLDER);

                    oneUser.setCity(YamoUtils.DEFAULT_CITY);
                    oneUser.setLat(YamoUtils.DEFAULT_LOCATION_LAT);
                    oneUser.setLon(YamoUtils.DEFAULT_LOCATION_LON);
                    oneUser.setLocation(YamoUtils.DEFAULT_CITY);

                    oneUser.setEnabled(true);
                    oneUser.setVisible(true);
                    oneUser.setNickname(null);
                    oneUser.setNickNameEnabled(false);
                    oneUser.setSignUpCompleted(true);
                    oneUser.setReady(true);
                    oneUser.setUserType(UserType.USER);

                    userRepository.save(oneUser);

                    User twoUser = new User();
                    twoUser.setFirstName("Two");
                    twoUser.setLastName("Test");
                    twoUser.setEmail("two@yamo.com");
                    twoUser.setPassword(new BCryptPasswordEncoder().encode(adminPassword));
                    twoUser.setSecretCode(null);
                    twoUser.setFacebookId(null);
                    // FIXME Image should be uploaded to AWS S3 service.
                    twoUser.setProfileImageUrl(YamoUtils.DEFAULT_PROFILE_IMAGE_PLACEHOLDER);

                    twoUser.setCity(YamoUtils.DEFAULT_CITY);
                    twoUser.setLat(YamoUtils.DEFAULT_LOCATION_LAT);
                    twoUser.setLon(YamoUtils.DEFAULT_LOCATION_LON);
                    twoUser.setLocation(YamoUtils.DEFAULT_CITY);

                    twoUser.setEnabled(true);
                    twoUser.setVisible(true);
                    twoUser.setNickname(null);
                    twoUser.setNickNameEnabled(false);
                    twoUser.setSignUpCompleted(true);
                    twoUser.setReady(true);
                    twoUser.setUserType(UserType.USER);

                    userRepository.save(twoUser);

                    User threeUser = new User();
                    threeUser.setFirstName("Three");
                    threeUser.setLastName("Test");
                    threeUser.setEmail("three@yamo.com");
                    threeUser.setPassword(new BCryptPasswordEncoder().encode(adminPassword));
                    threeUser.setSecretCode(null);
                    threeUser.setFacebookId(null);
                    // FIXME Image should be uploaded to AWS S3 service.
                    threeUser.setProfileImageUrl(YamoUtils.DEFAULT_PROFILE_IMAGE_PLACEHOLDER);

                    threeUser.setCity(YamoUtils.DEFAULT_CITY);
                    threeUser.setLat(YamoUtils.DEFAULT_LOCATION_LAT);
                    threeUser.setLon(YamoUtils.DEFAULT_LOCATION_LON);
                    threeUser.setLocation(YamoUtils.DEFAULT_CITY);

                    threeUser.setEnabled(true);
                    threeUser.setVisible(true);
                    threeUser.setNickname(null);
                    threeUser.setNickNameEnabled(false);
                    threeUser.setSignUpCompleted(true);
                    threeUser.setReady(true);
                    threeUser.setUserType(UserType.USER);

                    userRepository.save(threeUser);

                    User fourUser = new User();
                    fourUser.setFirstName("Four");
                    fourUser.setLastName("Test");
                    fourUser.setEmail("four@yamo.com");
                    fourUser.setPassword(new BCryptPasswordEncoder().encode(adminPassword));
                    fourUser.setSecretCode(null);
                    fourUser.setFacebookId(null);
                    // FIXME Image should be uploaded to AWS S3 service.
                    fourUser.setProfileImageUrl(YamoUtils.DEFAULT_PROFILE_IMAGE_PLACEHOLDER);

                    fourUser.setCity(YamoUtils.DEFAULT_CITY);
                    fourUser.setLat(YamoUtils.DEFAULT_LOCATION_LAT);
                    fourUser.setLon(YamoUtils.DEFAULT_LOCATION_LON);
                    fourUser.setLocation(YamoUtils.DEFAULT_CITY);

                    fourUser.setEnabled(true);
                    fourUser.setVisible(true);
                    fourUser.setNickname(null);
                    fourUser.setNickNameEnabled(false);
                    fourUser.setSignUpCompleted(true);
                    fourUser.setReady(true);
                    fourUser.setUserType(UserType.USER);

                    userRepository.save(fourUser);

                }

                if (0 == venueRepository.count()) {

                    Venue newExhibition = new Venue();

                    newExhibition.setName("Joan Miró");
                    newExhibition.setVenueType(VenueType.EXHIBITION);
                    newExhibition.setAddress("52, Bishopgate. EC5 4XL, London");
                    newExhibition.setFee(26.50);
                    newExhibition.setLat(51.478688);
                    newExhibition.setLon(-0.204741);
                    newExhibition.setLocation("52, Bishopgate, London");
                    newExhibition.setOpeningTimes("9am-6pm");
                    newExhibition.setWebsite("http://www.joan-miro.net/");
                    newExhibition.setDescription("The Mayoral brothers, Eduard and Jordi, are directors of a gallery founded by their father in Barcelona in 1989 to showcase artists linked to the city, including Miró, who was born there in 1893.");

                    venueRepository.save(newExhibition);


                    Venue newGallery = new Venue();
                    newGallery.setName("Tate modern");
                    newGallery.setVenueType(VenueType.GALLERY);
                    newGallery.setAddress("Bankside, London SE1 9TG");
                    newGallery.setFee(26.50);
                    newGallery.setLat(51.508129);
                    newGallery.setLon(-0.095187);
                    newGallery.setLocation("Bankside, London SE1 9TG");
                    newGallery.setOpeningTimes("10am-6pm");
                    newGallery.setWebsite("http://www.tate.org.uk/visit/tate-modern");
                    newGallery.setDescription("Tate Modern is a modern art gallery located in London. It is Britain's national gallery of international modern art and forms part of the Tate group (together with Tate Britain, Tate Liverpool, Tate St Ives and Tate Online).");

                    venueRepository.save(newGallery);

                }

                if (0 == mediumRepository.count()) {

                    List<Medium> lstMediums = Arrays.asList(
                            new Medium("Adhesives", "Adhesives", "http://www.eecni.com/wp-content/uploads/2010/10/eec-adhesives.jpg"),
                            new Medium("Wood", "Wood", "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/16_wood_samples.jpg/350px-16_wood_samples.jpg"),
                            new Medium("Ceramics", "Ceramics", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/78/Conner-prairie-pottery-rack.jpg/800px-Conner-prairie-pottery-rack.jpg"),
                            new Medium("Pottery", "Pottery", "http://www.towntalk.co.uk/subdomains/lib/image.php/aldershot35493.jpg?domain=.co.uk&image=http://www.aldershot.towntalk.co.uk/images_folder/eventsimg/aldershot35493.jpg"),
                            new Medium("Airbrush", "Airbrush", "https://upload.wikimedia.org/wikipedia/commons/9/94/PaascheAirbrush.jpg"),
                            new Medium("Acrylic paint", "Acrylic paint", "http://www.artdiscount.co.uk/media/catalog/product/cache/1/image/9df78eab33525d08d6e5fb8d27136e95/a/d/ad-paint-1.jpg"),
                            new Medium("Chalk", "Chalk", "http://www.waltersandwalters.co.uk/image/data/chalk/monster-chalks.jpg"),
                            new Medium("Charcoal", "Charcoal", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSRz7ZNG3fgEtmXsIWoretuS7uZMu_zB9TvuhlddSeH9yWL-DKc"),
                            new Medium("Crayon", "Crayon", "http://s4.thcdn.com/productimg/0/600/600/23/30003623-1286539791-237000.jpg"),
                            new Medium("Gouache", "Gouache", "http://static1.squarespace.com/static/5005c1c4c4aa8b4d9761f99f/54d00bc1e4b03b2209cabd80/54d00bc4e4b03b2209cad791/1422922197227/130807_palette_0006.jpg"),
                            new Medium("Human finger", "Human finger", "https://nourishingourchildren.files.wordpress.com/2013/02/fingerpainting.jpg"),
                            new Medium("Oil paint", "Oil paint", "http://paintoutside.com/wp/wp-content/uploads/2015/04/Oil-paint_tubes.jpg"),
                            new Medium("Pastel", "Pastel", "http://www.jerrysartarama.com/images/products/pastels/semi_hard_pastels/masters_watersoluble_pastel_painting_sticks/0086952000000-st-01-masters-water-soluble-pastel-painting-sticks--set-of-24.jpg"),
                            new Medium("Sand", "Sand", "http://www.kinderart.com/multic/sandpainting2.jpg"),
                            new Medium("Tempera", "Tempera", "http://tshop.r10s.com/1c7a4250-f8e1-11e1-a4c3-005056bd775e/20140313/162908ce-efd4-4541-bc1d-3a3cd963dc14.jpg"),
                            new Medium("Watercolour", "Watercolour", "https://sallywakelin.files.wordpress.com/2012/01/243-drops.jpg")
                    );
                    mediumRepository.save(lstMediums);

                }

                if (0 == movementRepository.count()) {

                    List<Movement> lstMovements = Arrays.asList(
                            new Movement("Abstract art", "Abstract art", "https://vaibhavbhakare.files.wordpress.com/2015/11/abstract-art.jpg"),
                            new Movement("Abstract expressionism", "Abstract expressionism", "http://danielspencer.ie/blog/wp-content/uploads/2012/12/photo.jpg"),
                            new Movement("Aestheticism", "Aestheticism", "https://upload.wikimedia.org/wikipedia/commons/3/3c/Design_for_an_Aesthetic_theatrical_poster.png"),
                            new Movement("Analytical art", "Analytical art", "https://s-media-cache-ak0.pinimg.com/236x/28/c7/76/28c776d469fe185dddf83a672a15b885.jpg"),
                            new Movement("Anti-realism", "Anti-realism", "https://larvalsubjects.files.wordpress.com/2010/02/escher-mc-hand-with-globe-7400026.jpg"),
                            new Movement("Assemblage", "Assemblage", "http://www.kirklandsmith.com/wp-content/gallery/assemblages/Marilyn.jpg"),
                            new Movement("Baroque", "Baroque", "http://www.visitportoandnorth.travel/var/porto_norte/storage/images/blog/baroque-treasures-in-porto-and-the-north-of-portugal/458580-1-eng-GB/Baroque-treasures-in-Porto-and-the-North-of-Portugal.jpg"),
                            new Movement("Contextual modernism", "Contextual modernism", "https://plus91archivesblog.files.wordpress.com/2013/03/p1010239.jpg"),
                            new Movement("Conceptual art", "Conceptual art", "http://www.hardawayart.com/uploads/1/9/1/3/1913193/7113251_orig.jpg"),
                            new Movement("Cubism", "Cubism", "http://dimamp.com/wp-content/gallery/art-cubsim/daa15f4ab93a4a3244bfeffb929bcdb8.jpg"),
                            new Movement("Feminist art", "Feminist art", "http://feministartproject.rutgers.edu/media/uploads/Girl_House_and_Beyond.jpg"),
                            new Movement("Futurism", "Futurism", "http://exhibitions.guggenheim.org/futurism/content/images/futurism_landing_depero.jpg"),
                            new Movement("Graffiti", "Graffiti", "http://d2jv9003bew7ag.cloudfront.net/uploads/00-Kenny-Scharf-865x600.jpg"),
                            new Movement("Hypermodernism", "Hypermodernism", "http://america.pink/images/2/0/2/7/2/5/3/en/2-hypermodernism-art.jpg"),
                            new Movement("Impressionism", "Impressionism", "https://ka-perseus-images.s3.amazonaws.com/a8104c7a75391341cfaaf0829d03c0f95280008b.jpg"),
                            new Movement("Metaphysical painting", "Metaphysical painting", "https://upload.wikimedia.org/wikipedia/en/1/1b/De_Chirico's_Love_Song.jpg"),
                            new Movement("Rococo", "Rococo", "https://newtonsfurniture.co.uk/media/catalog/product/cache/1/image/1100x/040ec09b1e35df139433887a97daa66f/r/o/rococo_parisienne_cream-french_bed_sq_10.jpeg")
                    );
                    movementRepository.save(lstMovements);
                }

                if (0 == contentRepository.count()) {

                    Movement movement = movementRepository.findOne(3L);
                    Content content01 = new Content();
                    content01.setTitle(movement.getTitle());
                    content01.setDescription(movement.getDescription());
                    content01.setContentType(ContentType.MOVEMENT);
                    content01.setImageUrl(movement.getImageUrl());
                    content01.setMovement(movement);
                    contentRepository.save(content01);

                    movement = movementRepository.findOne(6L);
                    Content content02 = new Content();
                    content02.setTitle(movement.getTitle());
                    content02.setDescription(movement.getDescription());
                    content02.setContentType(ContentType.MOVEMENT);
                    content02.setImageUrl(movement.getImageUrl());
                    content02.setMovement(movement);
                    contentRepository.save(content02);

                    movement = movementRepository.findOne(4L);
                    Content content03 = new Content();
                    content03.setTitle(movement.getTitle());
                    content03.setDescription(movement.getDescription());
                    content03.setContentType(ContentType.MOVEMENT);
                    content03.setImageUrl(movement.getImageUrl());
                    content03.setMovement(movement);
                    contentRepository.save(content03);

                    movement = movementRepository.findOne(5L);
                    Content content04 = new Content();
                    content04.setTitle(movement.getTitle());
                    content04.setDescription(movement.getDescription());
                    content04.setContentType(ContentType.MOVEMENT);
                    content04.setImageUrl(movement.getImageUrl());
                    content04.setMovement(movement);
                    contentRepository.save(content04);

                    movement = movementRepository.findOne(8L);
                    Content content05 = new Content();
                    content05.setTitle(movement.getTitle());
                    content05.setDescription(movement.getDescription());
                    content05.setContentType(ContentType.MOVEMENT);
                    content05.setImageUrl(movement.getImageUrl());
                    content05.setMovement(movement);
                    contentRepository.save(content05);

                    Medium medium = mediumRepository.findOne(2L);
                    Content content06 = new Content();
                    content06.setTitle(medium.getTitle());
                    content06.setDescription(medium.getDescription());
                    content06.setContentType(ContentType.MEDIUM);
                    content06.setImageUrl(medium.getImageUrl());
                    content06.setMedium(medium);
                    contentRepository.save(content06);

                    medium = mediumRepository.findOne(5L);
                    Content content07 = new Content();
                    content07.setTitle(medium.getTitle());
                    content07.setDescription(medium.getDescription());
                    content07.setContentType(ContentType.MEDIUM);
                    content07.setImageUrl(medium.getImageUrl());
                    content07.setMedium(medium);
                    contentRepository.save(content07);

                    medium = mediumRepository.findOne(6L);
                    Content content08 = new Content();
                    content08.setTitle(medium.getTitle());
                    content08.setDescription(medium.getDescription());
                    content08.setContentType(ContentType.MEDIUM);
                    content08.setImageUrl(medium.getImageUrl());
                    content08.setMedium(medium);
                    contentRepository.save(content08);

                    medium = mediumRepository.findOne(3L);
                    Content content09 = new Content();
                    content09.setTitle(medium.getTitle());
                    content09.setDescription(medium.getDescription());
                    content09.setContentType(ContentType.MEDIUM);
                    content09.setImageUrl(medium.getImageUrl());
                    content09.setMedium(medium);
                    contentRepository.save(content09);

                    medium = mediumRepository.findOne(1L);
                    Content content10 = new Content();
                    content10.setTitle(medium.getTitle());
                    content10.setDescription(medium.getDescription());
                    content10.setContentType(ContentType.MEDIUM);
                    content10.setImageUrl(medium.getImageUrl());
                    content10.setMedium(medium);
                    contentRepository.save(content10);

                }

            }
        };
    }

    @Bean
    public JavaMailSenderImpl mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setProtocol(mailProtocol);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        mailSender.setDefaultEncoding("UTF-8");

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtps.auth", mailAuth);
        javaMailProperties.put("mail.smtp.ssl.enable", mailSsl);
        javaMailProperties.put("mail.transport.protocol", mailProtocol);
        javaMailProperties.put("mail.from", from);
        mailSender.setJavaMailProperties(javaMailProperties);

        return mailSender;
    }

    @Bean
    public AwsS3Service awsS3Service() {
        return new AwsS3Service(key, secret, s3endpoint, bucketName);
    }

    @Bean
    public AwsSNSService snsService() {
        return new AwsSNSService(key, secret, snsEndpoint);
    }

}
