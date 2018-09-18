.. _add credentials to jenkins:

--------------------------
Add Credentials to Jenkins
--------------------------

In Jenkins, credentials are used to allow the Jenkins instance to interact with 3rd party applications and websites.

While there are several kinds of credentials in Jenkins, we will only be working with global credentials, which can be used from anywhere within the Jenkins instance.

The steps to add credentials to your Jenkins instance are as follows:

1. On the Jenkins Homepage, click **Credentials** **>** **System** on the left.
2. In **System**, click the text **Global credentials (unrestricted)** within the centered table under the domain header.
3. On the left, click **Add Credentials**.
4. Click the dropdown labeled **Kind** and select **Username with password** if it's not already selected.
5. Click the dropdown labeled **Scope** and select **Global (Jenkins, nodes, items, all child items, etc)** if it's not already selected.
6. Enter the values for the **Username**, **Password**, and **ID** fields as denoted by the sections that require a Jenkins credential be added.
7. Click the **OK** button.
8. Verify that the credential that you just created can now be seen in the list of credentials with the correct name.
