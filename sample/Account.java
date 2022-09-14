import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "accounts")
public class Account
{
    @Column(name = "id")
    private int id;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "account_number")
    private int accountNumber;

    @Column(name = "userid")
    private int userid;

    @Column(name = "balance")
    private double balance;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    public Account()
    {
    }

    public Account(int id, String accountType, int accountNumber, int userid, double balance, String firstName, String lastName)
    {
        this.id = id;
        this.accountType = accountType;
        this.accountNumber = accountNumber;
        this.userid = userid;
        this.balance = balance;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getAccountType()
    {
        return accountType;
    }

    public void setAccountType(String accountType)
    {
        this.accountType = accountType;
    }

    public int getAccountNumber()
    {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber)
    {
        this.accountNumber = accountNumber;
    }

    public int getUserid()
    {
        return userid;
    }

    public void setUserid(int userid)
    {
        this.userid = userid;
    }

    public double getBalance()
    {
        return balance;
    }

    public void setBalance(double balance)
    {
        this.balance = balance;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Account))
        {
            return false;
        }
        Account account = (Account) o;
        return getId() == account.getId() && getAccountNumber() == account.getAccountNumber() && getUserid() == account.getUserid() && Double.compare(account.getBalance(), getBalance()) == 0 && Objects.equals(getAccountType(), account.getAccountType()) && Objects.equals(getFirstName(), account.getFirstName()) && Objects.equals(getLastName(), account.getLastName());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getId(), getAccountType(), getAccountNumber(), getUserid(), getBalance(), getFirstName(), getLastName());
    }

    @Override
    public String toString()
    {
        return "Account{" +
                "id=" + id +
                ", accountType='" + accountType + '\'' +
                ", accountNumber=" + accountNumber +
                ", userid=" + userid +
                ", balance=" + balance +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
