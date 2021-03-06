package com.puhuilink.qbs.core.base.exception;

import com.puhuilink.qbs.core.base.enums.IResultCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BizException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 错误码
     */
    protected String errorCode;
    /**
     * 错误信息
     */
    protected String errorMsg;

    public BizException(IResultCode resultCode) {
        super(resultCode.getCode());
        this.errorCode = resultCode.getCode();
        this.errorMsg = resultCode.getMsg();
    }

    public BizException(IResultCode resultCode, String message) {
        super(message);
        this.errorCode = resultCode.getCode();
        this.errorMsg = message;
    }

    public BizException(IResultCode resultCode, Throwable cause) {
        super(resultCode.getCode(), cause);
        this.errorCode = resultCode.getCode();
        this.errorMsg = resultCode.getMsg();
    }

    public BizException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public BizException(IResultCode resultCode, String errorMsg, Throwable cause) {
        super(resultCode.getCode(), cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
