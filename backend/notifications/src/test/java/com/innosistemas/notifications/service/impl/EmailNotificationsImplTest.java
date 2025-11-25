package com.innosistemas.notifications.service.impl;

import org.junit.jupiter.api.Test;

class EmailNotificationsImplTest {
    EmailNotificationsImpl emailNotificationsImpl = new EmailNotificationsImpl();

    @Test
    void testSendEmail() {
        emailNotificationsImpl.sendEmail("email", "subject", "content");
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme