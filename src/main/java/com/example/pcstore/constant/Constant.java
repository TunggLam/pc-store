package com.example.pcstore.constant;

public class Constant {

    public static final String EXCEPTION_MESSAGE_DEFAULT = "Dịch vụ tạm thời gián đoạn. Vui lòng thử lại sau ít phút.";

    public static final String USERNAME_EXISTS = "Tài khoản đã được sử dụng. Vui lòng thử lại bằng tài khoản khác.";

    public static final String OTP_EXPIRED_OR_INVALID_MES = "Mã OTP chưa chính xác. Vui lòng kiểm tra lại.";

    public static final String OTP_VERIFY_NULL = "Vui lòng xác thực OTP để đăng kí tài khoản.";

    public static final String VERIFY_OTP_5TH = "Mã xác thực đã bị hủy. Bạn chủ có thể lấy mã xác thực mới sau 24h nữa.";

    public static final String PASSWORD_DEFAULT = "123";

    public static final String ERROR_REGISTER_USER = "Có lỗi trong quá trình đăng ký tài khoản. Vui lòng thử lại.";

    public static final String OTP_EXCEEDED = "Mã xác thực chỉ được gửi tối đa 3 lần trong 1 ngày.";

    public static final String VERIFY_OTP_BLOCKED_MESS = "Bạn đã nhập sai mã OTP %s lần liên tiếp, vui lòng thử lại sau 5 phút.";

    public static final String USER_NOT_EXIST = "Tài khoản không tồn tại. Vui lòng kiểm tra lại";

    public static final String USERNAME_AND_PASSWORD_EMPTY = "Tài khoản và mật khẩu không được bỏ trống";

    public static final String USERNAME_AND_PASSWORD_NOT_EXIST = "Tài khoản chưa được đăng ký. Vui lòng đăng ký tài khoản để tiếp tục";

    public static final String USERNAME_AND_PASSWORD_IS_BLOCK = "Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên để được hỗ trợ";

    public static final String USERNAME_AND_PASSWORD_WRONG = "Tài khoản và mật khẩu không đúng. Vui lòng nhập lại";

    public static final String ROLE_NO_RIGHT = "Bạn không có quyền truy cập chức năng này. Vui lòng đăng nhập lại";

    public static final String PRODUCT_EMPTY = "Tên loại sản phẩm không được để trống";

    public static final String PRODUCT_EXIST = "Loại sản phẩm này đã tồn tại trong hệ thống";

    public static final String ID_EMPTY = "Id loại sản phẩm không được bỏ trống";

    public static final String EMAIL_EXISTS = "Địa chỉ email đã sử dụng. Vui lòng thử lại bằng email khác.";

    public static final String EMAIL_NOT_EXISTS = "Không tìm thấy địa chỉ email. Vui lòng kiểm tra lại email.";

    public static final String PHONE_EXISTS = "Số điện thoại đã sử dụng. Vui lòng thử lại bằng số điện thoại khác.";

    public static final String X_FORWARDED_FOR = "x-forwarded-for";

    public static final String PRODUCT_NOT_FOUND = "Không tìm thấy thông tin sản phẩm";

    public static final String KEY_GENERATOR = "ecommerce-api-key";

    public static final String ORDER_NOT_FOUND = "Không tìm thấy thông tin đơn hàng";

    public static final String ORDER_STATUS_INVALID = "Trạng thái đơn hàng không hợp lệ";

    public static final String ORDER_STATUS_CANNOT_UPDATE = "Không thể cập nhật trạng thái đơn hàng đã hoàn thành hoặc đã hủy";

}
