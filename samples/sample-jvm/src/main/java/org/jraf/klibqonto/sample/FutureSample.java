/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2019-present Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jraf.klibqonto.sample;

import org.jraf.klibqonto.client.*;
import org.jraf.klibqonto.client.future.FutureQontoClient;
import org.jraf.klibqonto.client.future.FutureQontoClientUtils;
import org.jraf.klibqonto.model.attachments.Attachment;
import org.jraf.klibqonto.model.attachments.AttachmentType;
import org.jraf.klibqonto.model.attachments.file.FileAttachmentByteInput;
import org.jraf.klibqonto.model.dates.DateRange;
import org.jraf.klibqonto.model.labels.Label;
import org.jraf.klibqonto.model.memberships.Membership;
import org.jraf.klibqonto.model.organizations.Organization;
import org.jraf.klibqonto.model.pagination.Page;
import org.jraf.klibqonto.model.pagination.Pagination;
import org.jraf.klibqonto.model.transactions.Transaction;

import java.util.*;
import java.util.stream.Collectors;

import static org.jraf.klibqonto.sample.UtilsKt.date;

class FutureSample {

    // !!!!! DO THIS FIRST !!!!!
    // Replace these constants with your login / secret key
    // that you will find in the Qonto web application under Settings, in the "Integrations (API)" section.
    private static final String LOGIN = "xxx";
    private static final String SECRET_KEY = "yyy";

    // Replace this with a transaction internal id that exists
    private static final String TRANSACTION_INTERNAL_ID = "00000000-0000-0000-0000-000000000000";

    // Replace this to a path to a pdf file that exists
    private static final String PATH_TO_A_PDF_FILE = "/tmp/file.pdf";

    private FutureQontoClient client;

    private void initClient() {
        QontoClient qontoClient = QontoClient.newInstance(
                new ClientConfiguration(
                        new LoginSecretKeyAuthentication(
                                LOGIN,
                                SECRET_KEY
                        ),
                        new HttpConfiguration(
                                // Uncomment to see more logs
                                // HttpLoggingLevel.BODY,
                                HttpLoggingLevel.NONE,
                                // This is only needed to debug with, e.g., Charles Proxy
                                new HttpProxy("localhost", 8888)
                        )
                )
        );
        client = FutureQontoClientUtils.asFutureQontoClient(qontoClient);
    }

    private void main() throws Exception {
        // Init client
        initClient();

        try {
            // Get organization
            System.out.println("Organization:");
            Organization organization = client.getOrganizations().getOrganization().get();
            System.out.println(organization);

            // Get first page of memberships
            System.out.println("\n\nMemberships:");
            Page<Membership> membershipList = client.getMemberships().getMembershipList(new Pagination()).get();
            System.out.println(asLines(membershipList.getItems()));

            // Get first page of labels
            System.out.println("\n\nLabels:");
            Page<Label> labels = client.getLabels().getLabelList(new Pagination()).get();
            System.out.println(asLines(labels.getItems()));

            // Get first 2 pages of transactions
            System.out.println("\n\nTransactions:");
            List<Transaction> transactionList = getTransactionList(organization);
            System.out.println(asLines(transactionList.stream().map(UtilsKt::toFormattedString).collect(Collectors.toList())));

            // Get the first attachment from the transaction list
            System.out.println("\n\nAttachment:");
            Attachment attachment = getAttachment(transactionList);
            System.out.println(attachment);

            // Get all the attachments of a specific transaction
            System.out.println("\n\nAttachments of transaction:");
            List<Attachment> attachmentList = client.getAttachments().getAttachmentList(TRANSACTION_INTERNAL_ID).get();
            System.out.println(attachmentList);

            // Remove the last attachment
            String attachmentId = attachmentList.get(attachmentList.size() - 1).getId();
            client.getAttachments().removeAttachment(TRANSACTION_INTERNAL_ID, attachmentId);
            System.out.println("Attachment " + attachmentId + " removed");

            // Add an attachment
            client.getAttachments().addAttachment(
                    TRANSACTION_INTERNAL_ID,
                    AttachmentType.PDF,
                    new FileAttachmentByteInput(PATH_TO_A_PDF_FILE)
            ).get();
            System.out.println("Attachment added");

            // Remove all attachments
//            client.getAttachments().removeAllAttachments(TRANSACTION_INTERNAL_ID).get();
//            System.out.println("All attachments removed");
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // Close
            client.close();
        }

        // Exit process
        System.exit(0);
    }

    private List<Transaction> getTransactionList(Organization organization) throws Exception {
        // 1/ Get first page of transactions
        String slug = organization.getBankAccounts().get(0).getSlug();
        Page<Transaction> firstPage = client.getTransactions().getTransactionList(
                slug,
                EnumSet.of(Transaction.Status.COMPLETED, Transaction.Status.DECLINED),
                new DateRange(date("2018-01-01"), date("2019-12-31")),
                null,
                QontoClient.Transactions.SortField.UPDATED_DATE,
                QontoClient.Transactions.SortOrder.DESCENDING,
                new Pagination(0, 10)
        ).get();
        ArrayList<Transaction> list = new ArrayList<>(firstPage.getItems());

        // 2/ Get next page of transactions (if any)
        Pagination nextPagination = firstPage.getNextPagination();
        if (nextPagination != null) {
            Page<Transaction> secondPage = client.getTransactions().getTransactionList(
                    slug,
                    EnumSet.of(Transaction.Status.COMPLETED, Transaction.Status.DECLINED),
                    new DateRange(date("2018-01-01"), date("2019-12-31")),
                    null,
                    QontoClient.Transactions.SortField.UPDATED_DATE,
                    QontoClient.Transactions.SortOrder.DESCENDING,
                    nextPagination
            ).get();
            list.addAll(secondPage.getItems());
        }
        return list;
    }

    private Attachment getAttachment(List<Transaction> transactionList) throws Exception {
        // Get the first attachment id of the first transaction that has at least one
        Optional<Transaction> firstTransactionWithAttachment = transactionList.stream()
                .filter(transaction -> !transaction.getAttachmentIds().isEmpty())
                .findFirst();
        if (!firstTransactionWithAttachment.isPresent()) return null;
        String firstAttachmentId = firstTransactionWithAttachment.get().getAttachmentIds().get(0);
        // Call getAttachment from the id
        return client.getAttachments().getAttachment(firstAttachmentId).get();
    }


    private static <T> String asLines(Collection<T> c) {
        return c.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
    }

    public static void main(String[] av) throws Exception {
        new FutureSample().main();
    }
}
