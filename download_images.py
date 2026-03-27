import os
import requests
from bs4 import BeautifulSoup
from urllib.parse import urljoin, urlparse

def download_images(url, folder_name="downloaded_images"):
    # Create the folder if it doesn't exist
    if not os.path.exists(folder_name):
        os.makedirs(folder_name)

    # Add a User-Agent to avoid being blocked by some websites
    headers = {"User-Agent": "Mozilla/5.0"}

    try:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        soup = BeautifulSoup(response.text, 'html.parser')

        # Find all <img> tags
        img_tags = soup.find_all('img')
        print(f"Found {len(img_tags)} images. Starting download...")

        for img in img_tags:
            img_url = img.get('src')
            if not img_url:
                continue

            # Handle relative URLs (e.g., /images/logo.png)
#             img_url = urljoin(url, img_url)

            # Clean the filename from the URL
            filename = os.path.basename(urlparse(img_url).path)
            if not filename:
                continue

            file_path = os.path.join(folder_name, filename)

            # Download and save the image
            with open(file_path, 'wb') as f:
                img_data = requests.get(img_url, headers=headers).content
                f.write(img_data)
                print(f"Saved: {filename}")

    except Exception as e:
        print(f"An error occurred: {e}")

# Usage
target_url = "https://vnexpress.net"
download_images(target_url)