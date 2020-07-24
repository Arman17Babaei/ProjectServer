### Replay Attack
توکن بعد از دریافت هر پیغام تغییر می‌کند. به همین علت درخواست‌های قبلی فاقد ارزش هستند.

### Denial of Service Attack
در صورتی که از یک آدرس آی پی در دقیقه‌ی گذشته بیش از صد درخواست وارد سرور شده باشد، درخواست جدید در محاسبه‌ی تعداد درخواست‌ها اعمال شده و با کد ۴۲۹ به درخواست پاسخ داده می‌شود.

همچنین بیش از دویست درخواست برای هر کلاینت نگه‌داری نمی‌شود، یعنی تعداد رد پای آدرس آی پی مخرب در رم بیش از یک کیلوبایت نخواهد بود. (هر چند نبود این مورد هم عملا اخلالی ایجاد نمی‌کرد)

### Brute Force Attack
مشابه DoS با این تفاوت که بحداکثر تعداد درخواست‌ها در دقیقه پنجاه تا، و تنها درخواست‌هایی که موفق نبوده‌اند شمرده می‌شوند.

### Improper Inputs
قبل از این که اطلاعات در سمت سرور ثبت شوند چک می‌شود که اشیا‌ حاصل ار آن‌ها معتبر باشند. (اشیا ساخته می‌شوند و در صورتی که وروی معتبر نباشد این امر مقدور نیست.)

### Broken Authentication
توکن‌های پویا برای کاربران وارد شده، جلوگیری از وارد کردن متوالی پسورد.
