# Contributors Guide

Thank you for your interest in contributing to the MessEaseApp! Please follow the steps below to set up the project and contribute effectively.

## Forking and Cloning the Repository

1. **Fork the Repository:**
   - Go to the [MessEaseApp repository](https://github.com/iiitl/MessEase).
   - Click on the `Fork` button in the top-right corner of the page.

2. **Clone the Forked Repository:**
   - Open Android Studio.
   - Select `File > New > Project from Version Control > Git`.
   - Enter the URL of your forked repository (e.g., `https://github.com/YOUR_USERNAME/MessEaseApp.git`).
   - Click `Clone`.

## Setting Up Firebase

1. **Create a New Firebase Project:**
   - Go to the [Firebase Console](https://console.firebase.google.com/).
   - Click on `Create a Firebase Project` and follow the instructions to create a new project.
   - In `Enter your project name` enter "MessEase"
   - Click `Continue` till you reach Configure Google Analytics.
   - Choose "India" in `Analytics Location`
   - Check the checkbox `I accept the Google Analytics terms" and click `Create Project`
   - Click `Continue`

2. **Add an Android App to Your Firebase Project:**
   - In the Firebase Console, click on `Add app` and select the Android icon.
   - Register your app with the package name (`com.theayushyadav11.MessEase`).
   - Click on next skipping other things.
   - Click `Continue to console`

3. **Enable Firebase Services:**
   - **Authentication:**
     - Go to the `Authentication` section from the Drawer in the `Build` in the Firebase Console.
     - Click on `Get started`
     - Click on the `Sign-in method` tab and enable `Email/Password` and `Google` sign-in methods.
     - Use your own email for `Support email` and click on save.
   - **Firestore Database:**
     - Go to the `Firestore Database` section and click on `Create database`.
     - Select the `Location` as `asia-south2(Delhi) nam5(United States)` and click next.
     - Select `Start in production mode` and follow the instructions.
     - Go to `Rules`  and change the `false` to `true` and click `Publish`
  

4. **Add SHA Keys:**
   - In the Firebase Console, go to `Project settings` > `General` > `Your apps`.
   - Click on `Add fingerprint` and add your SHA-1 and SHA-256 keys. You can find these keys in Android Studio:
     - Go to the terminal in android studio and run this command:
       ```
       ./gradlew signingreport
   - If it shows any error it means that your jdk is not properly set up.
     Watch this to set up JDK properly [Tutorial Video](https://youtu.be/R6MoDMASwag?si=_RAOa6wl3-YyChOq).
      - Copy the SHA-1 and SHA-256 keys from the report and add them to Firebase.
      - After adding SHA-1 and SHA-256 keys and then download the `google-services.json` file provided and place it in the `app` directory of your project, select `Project` to view a proper file structure.

5. **Set Up Google Cloud Console:**
   - Go to the [Google Cloud Console](https://console.cloud.google.com/).
   - Select your Firebase project.
   - Go to `APIs & Services` > `Credentials`.
   - Click on `Create credentials` and select `Service account`.
   - follow the steps and a new account will be created.
   - click on it and then there will be an option for `Add key` > `Create new key` select `JSON`
   - Download the JSON key file and rename it to `messease.json` and place it in the `app/src/main/res/raw` directory of your project.

6. **Run the App:**
   - Sync your project with Gradle files.
   - Build and run the app on your emulator or physical device.
   

## Contributing Guidelines

1. **Ask for the Issue to Be Assigned:**
   - Before working on any issue, please make sure to comment on the issue and ask for it to be assigned to you.

2. **Create a New Branch for Each Issue:**
   - Once the issue is assigned to you, create a new branch for your work:
     ```
     git checkout -b issue-<issue-number>
     ```

3. **Solve the Issue:**
   - Make your changes and commit them with a clear and concise commit message:
     ```
     git commit -m "Fixes issue #<issue-number>: <description of fix>"
     ```

4. **Push the Changes:**
   - Push your branch to your forked repository:
     ```
     git push origin issue-<issue-number>
     ```

5. **Create a Pull Request:**
   - Go to the original repository and click on `Pull requests`.
   - Click on `New pull request` and select your branch.
   - Provide a detailed description of your changes and submit the pull request.

We appreciate your contributions and look forward to working with you!

Thank you!
