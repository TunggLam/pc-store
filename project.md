# PC Store Backend — Project Documentation

## 1. Tổng quan

**Tên project:** pc-store-backend (ecommerce-website)
**Mô tả:** Backend REST API cho hệ thống bán PC/linh kiện máy tính
**Server port:** 9999
**Java version:** 17
**Spring Boot:** 3.3.0
**Build tool:** Maven
**Main package:** `com.example.pcstore`
**Entry point:** `PcStoreBackendApplication.java`

---

## 2. Tech Stack

| Thành phần | Công nghệ | Chi tiết |
|---|---|---|
| Framework | Spring Boot 3.3.0 | Web, JPA, Security, Mail, Data Redis |
| Database | PostgreSQL | localhost:5432, db: pc_store |
| Authentication | Keycloak 24.0.0 | OAuth2/OIDC, port 8080, realm: master |
| JWT | Auth0 Java-JWT 4.4.0 | Token generation/validation |
| Cache | Redis + Redisson 3.18.1 | port 6379, token storage + object cache |
| File Storage | MinIO | port 9000 (API), 9001 (Console) |
| Image Hosting | Imgur API | client-id: 29f585fc0df678c |
| Payment | VNPay | Sandbox environment |
| Email | Gmail SMTP | port 587, TLS |
| DTO Mapping | MapStruct 1.5.3 | |
| Code Generation | Lombok | |
| API Docs | SpringDoc OpenAPI 2.5.0 | Swagger UI |

---

## 3. Cấu trúc thư mục

```
pc-store-backend/
├── src/main/java/com/example/pcstore/
│   ├── PcStoreBackendApplication.java
│   ├── aop/                        # AOP cho security
│   │   ├── Secured.java            # Custom annotation @Secured(role=RoleEnum.X)
│   │   └── SecuredAspect.java      # Before advice: validate JWT + role
│   ├── configurations/
│   │   ├── SecurityConfiguration.java   # Spring Security, CORS
│   │   ├── MinioConfiguration.java      # MinIO client
│   │   ├── MailConfiguration.java       # JavaMailSender
│   │   ├── RestTemplateConfiguration.java
│   │   └── OTPTypeEnumDeserializer.java
│   ├── constant/
│   │   └── Constant.java               # Error messages (tiếng Việt)
│   ├── controller/
│   │   ├── AuthenticationController.java
│   │   ├── ProductController.java
│   │   ├── CategoryController.java
│   │   ├── CartController.java
│   │   ├── PaymentController.java
│   │   ├── UserController.java
│   │   ├── AdminController.java
│   │   └── OTPController.java
│   ├── entity/
│   │   ├── BaseEntity.java             # Abstract: UUID id, createdAt, updatedAt
│   │   ├── Product.java
│   │   ├── Category.java
│   │   ├── Cart.java
│   │   ├── CartItem.java
│   │   ├── UserProfile.java
│   │   ├── UserProfileOTP.java
│   │   ├── PaymentVNPay.java
│   │   ├── PaymentHistory.java
│   │   └── ImgurUpload.java
│   ├── enums/
│   │   ├── RoleEnum.java               # USER, ADMIN, ALL, NON
│   │   └── OTPTypeEnum.java            # REGISTER, FORGOT_PASSWORD
│   ├── exception/
│   │   ├── BusinessException.java
│   │   ├── AuthenticationException.java
│   │   └── handler/
│   │       ├── RestExceptionHandler.java
│   │       └── model/ExceptionModel.java
│   ├── logging/
│   │   └── LoggingFactory.java
│   ├── mapper/
│   │   ├── ProductMapper.java
│   │   ├── CategoriesMapper.java
│   │   ├── AuthenticationMapper.java
│   │   └── UserProfileMapper.java
│   ├── model/
│   │   ├── request/                    # Request DTOs
│   │   ├── response/                   # Response DTOs
│   │   └── proxy/                      # Proxy models
│   ├── proxy/
│   │   └── KeycloakProxy.java          # Keycloak REST calls
│   ├── redis/
│   │   ├── RedisService.java           # Base Redis operations
│   │   └── TokenRedisService.java      # Token storage/retrieval
│   ├── repositories/
│   │   ├── ProductRepository.java
│   │   ├── CategoryRepository.java
│   │   ├── UserProfileRepository.java
│   │   ├── CartRepository.java
│   │   ├── CartItemRepository.java
│   │   ├── PaymentVNPayRepository.java
│   │   ├── UserProfileOTPRepository.java
│   │   ├── ImgurUploadRepository.java
│   │   └── spectification/
│   │       ├── ProductSpecification.java   # Filter by categoryId, name
│   │       ├── CategorySpecification.java
│   │       └── CartSpecification.java
│   ├── service/
│   │   ├── AuthenticationService.java  (interface)
│   │   ├── ProductService.java         (interface)
│   │   ├── CartService.java            (interface)
│   │   ├── CategoryService.java        (interface)
│   │   ├── PaymentService.java         (interface)
│   │   ├── KeycloakService.java        (interface)
│   │   ├── OTPService.java             (interface)
│   │   ├── UserProfileService.java     (interface)
│   │   ├── AdminService.java           (interface)
│   │   └── impl/                       # Service implementations
│   └── utils/
│       ├── JWTUtils.java               # Extract username/roles/token from JWT
│       ├── PasswordUtils.java
│       └── StringUtils.java
├── src/main/resources/
│   ├── application.yml                 # Main configuration
│   └── docker-compose.yml              # Infrastructure services
└── pom.xml
```

---

## 4. Cấu hình ứng dụng (application.yml)

```yaml
server:
  port: 9999

spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/pc_store
    username: postgres
    password: 12345678
  jpa:
    hibernate:
      ddl-auto: update

keycloak:
  realm: master
  auth-server-url: http://localhost:8080
  resource: pc-store
  ssl-required: none
  bearer-only: true
  public-client: true

spring.mail:
  host: smtp.gmail.com
  port: 587
  username: nguyentunglam230203@gmail.com

spring.data.redis:
  host: 127.0.0.1
  port: 6379
  database: 0
  timeout: 3600s

minio:
  url: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin123

vnpay:
  domain: https://sandbox.vnpayment.vn
  tmn-code: 71PRZR3X
  callback-url: http://localhost:4200/vnpay/confirm-payment
```

---

## 5. Database Schema (Entities)

### BaseEntity (Abstract)
- `id` — UUID, Primary Key (tự sinh)
- `createdAt` — LocalDateTime, auto-set on create
- `updatedAt` — LocalDateTime, auto-set on update

### Entities

| Entity | Table | Mô tả | Fields quan trọng |
|---|---|---|---|
| **Product** | products | Sản phẩm | name, description, price (BigDecimal), imageUrl, categoryId, quantity |
| **Category** | categories | Danh mục | name (unique), isActive |
| **UserProfile** | user_profiles | Tài khoản người dùng | keycloakId, firstName, lastName, email (unique), phoneNumber (unique), username (unique), password, isActive |
| **UserProfileOTP** | user_profile_otps | OTP xác thực | username, otp, type (REGISTER/FORGOT_PASSWORD), status, isVerified, countVerifyFalse, verifyAt |
| **Cart** | carts | Giỏ hàng | username, status (PENDING/...) |
| **CartItem** | cart_items | Item trong giỏ | username, productId, cartId, quantity, totalAmount |
| **PaymentVNPay** | payment_vnpay | Giao dịch VNPay | username, amount, transactionStatus, status, cardType, invoiceNo, transactionNo, ipAddress |
| **PaymentHistory** | payment_history | Lịch sử thanh toán | username, orderHistoryId, paymentId |
| **ImgurUpload** | imgur_uploads | Tracking upload ảnh | status, size, imgurUrl |

---

## 6. REST API Endpoints

**Base URL:** `http://localhost:9999/api`

### Authentication — `/api/authentication`
| Method | Path | Mô tả | Security |
|---|---|---|---|
| POST | `/register` | Đăng ký (cần OTP) | Public |
| POST | `/login` | Đăng nhập | Public |
| POST | `/logout` | Đăng xuất | @Secured(ALL) |
| POST | `/refresh` | Refresh JWT token | Public |
| POST | `/password` | Đổi mật khẩu | @Secured(ALL) |
| POST | `/forgot-password` | Quên mật khẩu | Public |

### Product — `/api`
| Method | Path | Mô tả | Security |
|---|---|---|---|
| GET | `/products?page=0&size=10&categoryId=&name=` | Danh sách sản phẩm | Public |
| GET | `/product/{id}` | Chi tiết sản phẩm | Public |
| POST | `/product` | Tạo sản phẩm | @Secured(ADMIN) |
| PUT | `/product/quantity` | Cập nhật số lượng | @Secured(ADMIN) |

### Category — `/api`
| Method | Path | Mô tả | Security |
|---|---|---|---|
| GET | `/categories?productCount=false` | Danh sách danh mục | Public |
| GET | `/category/{id}` | Chi tiết danh mục | @Secured(ADMIN) |
| POST | `/category` | Tạo danh mục | @Secured(ADMIN) |
| PUT | `/category/{id}` | Cập nhật danh mục | Public |
| DELETE | `/category/{id}` | Xóa danh mục | Public |

### Cart — `/api`
| Method | Path | Mô tả | Security |
|---|---|---|---|
| GET | `/cart` | Lấy giỏ hàng hiện tại | @Secured(USER) |
| GET | `/cart/history?cartId=` | Lịch sử giỏ hàng | @Secured(USER) |
| POST | `/cart` | Thêm vào giỏ | @Secured(USER) |
| PUT | `/cart` | Cập nhật giỏ hàng | @Secured(USER) |
| DELETE | `/cart/{cartId}/{productId}` | Xóa item khỏi giỏ | @Secured(USER) |

### Payment — `/api/payment`
| Method | Path | Mô tả | Security |
|---|---|---|---|
| POST | `/vnpay/init` | Khởi tạo đơn VNPay | @Secured(USER) |
| POST | `/vnpay/callback` | Callback từ VNPay | Public |
| GET | `/ipn` | IPN handler từ VNPay | Public |

### User — `/api/user`
| Method | Path | Mô tả | Security |
|---|---|---|---|
| GET | `/profile` | Lấy thông tin user | @Secured(USER) |

---

## 7. Luồng xử lý chính

### Đăng ký (Registration)
```
Client → POST /register (email, phone, username)
       → OTPService: gửi OTP qua Gmail SMTP
       → Client verify OTP → POST /authentication/verify-otp
       → AuthenticationService: tạo UserProfile trong PostgreSQL
       → KeycloakService: tạo user trên Keycloak
       → Return success
```

### Đăng nhập (Login)
```
Client → POST /login (username, password)
       → AuthenticationService: tìm UserProfile trong DB
       → KeycloakProxy: gọi Keycloak token endpoint
       → Keycloak trả về access_token + refresh_token
       → TokenRedisService: lưu access_token vào Redis
         Key: {username}_access_token_customers
       → Return LoginResponse (tokens + roles)
```

### Bảo mật Endpoint (@Secured)
```
Request → SecuredAspect (AOP @Before)
        → JWTUtils.getCurrentToken(): lấy token từ Authorization header
        → TokenRedisService: lấy expected token từ Redis
        → So sánh token (phải khớp)
        → JWTUtils.getRoles(): kiểm tra role
        → Nếu fail → throw AuthenticationException (HTTP 401)
        → Nếu pass → proceed với controller method
```

### Giỏ hàng (Cart)
```
User → POST /cart (productId, quantity)
     → CartService: tìm Cart với status=PENDING của user
     → Nếu chưa có → tạo Cart mới
     → Tạo CartItem (productId, quantity, totalAmount)
     → Return CartResponse
```

### Thanh toán VNPay
```
User → POST /payment/vnpay/init (amount, description)
     → PaymentService: tạo PaymentVNPay record trong DB
     → Generate VNPay payment URL (HMAC-SHA512 signature)
     → Return URL → Client redirect user đến VNPay

VNPay → GET /payment/ipn (params)
      → PaymentService: verify signature
      → Cập nhật PaymentVNPay.status
      → Tạo PaymentHistory record
```

---

## 8. Security Architecture

### Spring Security
- CORS: Allowed Origins `*`, Methods: GET/POST/PUT/DELETE
- CSRF: Disabled
- Default: tất cả endpoints `permitAll()`
- Fine-grained control qua `@Secured` annotation

### @Secured Annotation + AOP
```java
// Cách dùng
@Secured(role = RoleEnum.ADMIN)
public ResponseEntity<?> createProduct(...) { ... }

@Secured(role = RoleEnum.USER)
public ResponseEntity<?> getCart(...) { ... }

@Secured(role = RoleEnum.ALL)  // cả USER và ADMIN
public ResponseEntity<?> changePassword(...) { ... }
```

### Token Management (Redis)
- Access token key: `{username}_access_token_customers`
- Refresh token key: `{username}_refresh_token_customers`
- Expiry: theo cấu hình Keycloak

---

## 9. Caching Strategy

- Cache name: `"products"`, `"categories"`
- Key generator: `"ecommerce-api-key"`
- `@Cacheable` trên `findAll()` — cache kết quả query
- `@CacheEvict` trên `save()` — xóa cache khi có thay đổi

---

## 10. Exception Handling

### Exception Types
| Exception | HTTP Status | Khi nào dùng |
|---|---|---|
| `BusinessException` | 400 | Lỗi nghiệp vụ (user exist, product not found...) |
| `AuthenticationException` | 401 | Token invalid, role không đủ |
| `MethodArgumentNotValidException` | 400 | Validation fail trên request body |
| `Throwable` (catch-all) | 500 | Lỗi không mong đợi |

### Response Format
```json
{
  "message": "Mô tả lỗi",
  "code": "ERROR_CODE",
  "detail": "Chi tiết thêm",
  "description": "Mô tả bổ sung"
}
```

### Error Messages (Constant.java — tiếng Việt)
- `USERNAME_EXISTS`, `EMAIL_EXISTS`, `PHONE_EXISTS`
- `USERNAME_AND_PASSWORD_WRONG`
- `PRODUCT_NOT_FOUND`, `PRODUCT_EMPTY`, `PRODUCT_EXIST`
- `OTP_EXPIRED_OR_INVALID_MES`
- `VERIFY_OTP_5TH` — sau 5 lần sai OTP
- `VERIFY_OTP_BLOCKED_MESS` — bị rate limit (max 3 OTP/ngày)
- `EXCEPTION_MESSAGE_DEFAULT` — lỗi hệ thống chung

---

## 11. Infrastructure (Docker Compose)

File: `src/main/resources/docker-compose.yml`

```yaml
services:
  keycloak:
    image: quay.io/keycloak/keycloak:24.0.0
    container_name: keycloak-pc-store
    ports: ["8080:8080"]
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin

  redis:
    image: redis:latest
    container_name: redis-pc-store
    ports: ["6379:6379"]
    volumes: [redis_data:/data]

  minio:
    image: minio/minio:latest
    container_name: minio-pc-store
    ports: ["9000:9000", "9001:9001"]
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin123
    volumes: [minio_data:/data]

networks:
  pc-store-network:
    driver: bridge
```

---

## 12. Patterns & Conventions

### Service Pattern
- Luôn có **interface** + **impl** class
- Interface: `com.example.pcstore.service.XxxService`
- Impl: `com.example.pcstore.service.impl.XxxServiceImpl`

### Entity Pattern
- Tất cả extend `BaseEntity` (UUID id tự sinh, timestamps)
- Dùng Lombok `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`

### Repository Pattern
- Extend `JpaRepository<Entity, String>`
- Extend `JpaSpecificationExecutor<Entity>` cho advanced filtering
- Native queries với `@Query(nativeQuery = true)`

### DTO Pattern
- Request: `model/request/XxxRequest.java`
- Response: `model/response/XxxResponse.java`
- Mapping qua MapStruct: `mapper/XxxMapper.java`

### Controller Pattern
- `@RestController`, `@RequestMapping`
- Inject Service interface (không inject impl trực tiếp)
- Dùng `@Secured` annotation để bảo vệ endpoint

---

## 13. Utility Classes

| Class | Mô tả |
|---|---|
| `JWTUtils` | `getUsername()`, `getCurrentToken()`, `getRoles(token)` |
| `PasswordUtils` | Encode/decode password, Keycloak password handling |
| `StringUtils` | `isNullOrEmpty(str)` |
| `LoggingFactory` | Tạo logger chuẩn hóa |

---

## 14. Chạy local

### Yêu cầu
- Java 17+
- Docker (để chạy Keycloak, Redis, MinIO)
- PostgreSQL

### Khởi động infrastructure
```bash
cd src/main/resources
docker-compose up -d
```

### Chạy ứng dụng
```bash
./mvnw spring-boot:run
```

### Swagger UI
```
http://localhost:9999/swagger-ui.html
```
