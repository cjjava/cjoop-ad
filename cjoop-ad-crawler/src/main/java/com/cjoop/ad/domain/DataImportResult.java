package com.cjoop.ad.domain;

/**
 * 数据导入结果信息
 * 
 * @author 陈均
 *
 */
public class DataImportResult {
	private long startTime = 0;// 开始时间
	private long endTime = 0;// 结束时间
	private long successCount = 0;// 导入成功的数据
	private long totalCount=0;//总的数据
	private long failCount = 0;// 失败次数
	private long exeTime = 0;// 执行花费时间
	private int error = 0;// 0表示没有错误发生
	private String errorText = "";// 错误信息

	public long getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(long successCount) {
		this.successCount = successCount;
	}

	public long getFailCount() {
		return failCount;
	}

	public void setFailCount(long failCount) {
		this.failCount = failCount;
	}

	public long getExeTime() {
		return exeTime;
	}

	public void setExeTime(long exeTime) {
		this.exeTime = exeTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getErrorText() {
		return errorText;
	}

	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

}
