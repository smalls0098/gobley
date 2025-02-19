/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

use std::collections::HashMap;

#[derive(uniffi::Record)]
pub struct Response {
    pub status: u16,
    pub header: HashMap<String, String>,
    pub body: String,
}

#[uniffi::export(async_runtime = "tokio")]
async fn retrieve_from(url: String) -> Response {
    let response = reqwest::get(&url).await.unwrap();

    let status = response.status().as_u16();
    let header = response
        .headers()
        .iter()
        .map(|(name, value)| {
            (
                name.as_str().to_string(),
                String::from_utf8_lossy(value.as_bytes()).to_string(),
            )
        })
        .collect();
    let body = response.text().await.unwrap();
    Response {
        status,
        header,
        body,
    }
}

#[uniffi::export]
fn hash_string(s: String) -> String {
    blake3::hash(s.as_bytes()).to_string()
}

uniffi::setup_scaffolding!();
