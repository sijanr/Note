package dev.sijanrijal.note

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.util.*


import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*


class UtilFunctionsTest {

    @Nested
    @DisplayName("Email and Password test should")
    inner class EmailPasswordTest {

        @Test
        @DisplayName("Not allow passwords less than the minimum length")
        fun passwordLengthLessThanMinimum() {
            assertFalse(checkEmailPasswordValidity("johndoe@gmail.com","abcd")) {
                "Password is more than the required characters"
            }
        }

        @Test
        @DisplayName("Not allow email addresses that are less than 10 characters")
        fun emailAddressLengthLessThanMinimum(){
            assertFalse(checkEmailPasswordValidity("j@gmail", "abcdferysd")) {
                "Email address is more than the required characters"
            }
        }

        @Test
        @DisplayName("Allow email addresses and passwords that are at least of the required length")
        fun validEmailAddressAndPassword() {
            assertTrue(checkEmailPasswordValidity("john@gmail.com","asdfgdwerwihufd")) {
                "Email address or password length does not match the required length"
            }
        }
    }


    @Nested
    @DisplayName("User first and last name test should")
    inner class UserNameTest{

        @Test
        @DisplayName("Not allow null first and last names")
        fun nullFirstAndLastNames() {
            assertFalse(checkUserName(null, null)) {
                "User name is not null"
            }
        }

        @Test
        @DisplayName("Not allow user name that is empty")
        fun emptyFirstAndLastNames() {
            assertFalse(checkUserName("","")) {
                "User name is not blank"
            }
        }

        @Test
        @DisplayName("Not contain special characters or digits")
        fun specialCharactersAndDigitsUserName() {
            assertFalse(checkUserName("@#sfdad","243dsf")) {
                "User name does not contain special characters or digits"
            }
        }

        @Test
        @DisplayName("Allow valid user name")
        fun validUserName() {
            assertTrue(checkUserName("John", "Doe")) {
                "Not a valid user name"
            }
        }
    }

    @Nested
    @DisplayName("Date function converter should")
    inner class DateToStringFormat {

        private lateinit var date : Date

        @BeforeEach
        fun init() {
            date = Calendar.getInstance().time
        }

        @Test
        @DisplayName("Convert the month properly")
        fun convertDateToMonth() {
            val monthString = date.toString("MM")
            val monthInt = monthString.toInt()
            assertAll("Month",
                {assertNotNull(monthString)},
                {assertNotNull(monthInt)},
                { assertThat(monthInt, lessThanOrEqualTo(12))}
            )
        }

        @Test
        @DisplayName("Convert day properly")
        fun convertDateToDay() {
            val day = date.toString("dd").toInt()
            assertAll("Day",
                {assertNotNull(day)},
                { assertThat(day, lessThanOrEqualTo(31))}
            )
        }

        @Test
        @DisplayName("Convert year properly")
        fun convertDateToYear() {
            val year = date.toString("yyyy").toInt()
            assertAll("Year",
                {assertNotNull(year)},
                { assertEquals(2020, year)}
            )
        }
    }
}