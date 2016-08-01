import org.openqa.selenium.{By, WebDriver}
import org.openqa.selenium.chrome.ChromeDriver

/**
  * Created by cuz on 2016-08-01.
  */
object WebDriverTest {
  val driver:WebDriver=new ChromeDriver()
  val ele=driver.findElement(By.id("test1"))
}
