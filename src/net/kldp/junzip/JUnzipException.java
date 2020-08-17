/*
 * 작성된 날짜: 2005. 5. 5.
 */
package net.kldp.junzip;

/**
 * Junzip의 오류 예외
 * @author 손권남(kwon37xi@yahoo.co.kr)
 *
 */
public class JUnzipException extends Exception {
	/** 파일이 존재하지 않습니다. */
	public static final int FILE_NOT_FOUND = 1;
	
	/** 파일을 읽을 수 없습니다. */
	public static final int FILE_CANNOT_READ = 2;
	
	/** 디렉토리를 생성할 수 없습니다. */
	public static final int CANNOT_MKDIR = 3;
	
	/** 문자 인코딩이 올바르지 않습니다. */
	public static final int UNSUPPORTED_ENCODING = 4;
	
	/** 파일 생성 실패 */
	public static final int CANNOT_CREATE_FILE = 5;
	
	/** 그외의 오류 */
	public static final int ETC_ERROR = -1;
	
	/** 오류 코드 */
	private int errorCode = 0;

	/**
	 * 기본 예외 생성자
	 * @param errorCode 오류 코드
	 * @param message 오류 메시지
	 */
	public JUnzipException(int errorCode, String message) {
		super(message);
		
		this.errorCode = errorCode;
	}

	/**
	 * 오류의 원인을 포함하고 있는 예외 생성자
	 * @param errorCode 오류 코드
	 * @param message 오류 메시지
	 * @param cause 오류 원인 예외
	 */
	public JUnzipException(int errorCode, String message, Throwable cause) {
		super(message, cause);
		
		this.errorCode = errorCode;
	}
}
