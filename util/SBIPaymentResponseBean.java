package util;


import java.math.BigDecimal;

public class SBIPaymentResponseBean {

	private BigDecimal amount;

	private String bank_name;

	private Integer payment_Id;

	private String status;

	private String status_desc;

	private String ttype;

	private String sbi_ref_no;

	private String timestamp;

	public BigDecimal getAmount()
	{
		return amount;
	}

	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}

	public String getBank_name()
	{
		return bank_name;
	}

	public void setBank_name(String bank_name)
	{
		this.bank_name = bank_name;
	}

	public Integer getPayment_Id()
	{
		return payment_Id;
	}

	public void setPayment_Id(Integer payment_Id)
	{
		this.payment_Id = payment_Id;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getStatus_desc()
	{
		return status_desc;
	}

	public void setStatus_desc(String status_desc)
	{
		this.status_desc = status_desc;
	}

	public String getTtype()
	{
		return ttype;
	}

	public void setTtype(String ttype)
	{
		this.ttype = ttype;
	}

	public String getSbi_ref_no()
	{
		return sbi_ref_no;
	}

	public void setSbi_ref_no(String sbi_ref_no)
	{
		this.sbi_ref_no = sbi_ref_no;
	}

	public String getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(String timestamp)
	{
		this.timestamp = timestamp;
	}

	@Override
	public String toString()
	{
		return "SBIPaymentResponseBean [amount=" + amount + ", bank_name=" + bank_name + ", payment_Id=" + payment_Id + ", status=" + status + ", status_desc=" + status_desc + ", ttype=" + ttype + ", sbi_ref_no=" + sbi_ref_no + ", timestamp=" + timestamp + "]";
	}
}
