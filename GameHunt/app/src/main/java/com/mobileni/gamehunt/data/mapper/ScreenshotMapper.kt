package com.mobileni.gamehunt.data.mapper

import com.mobileni.gamehunt.data.model.ScreenshotItemResponse
import com.mobileni.gamehunt.domain.model.Screenshot


fun ScreenshotItemResponse.toDomain(): Screenshot {
    return Screenshot(
        id = id,
        imageUrl = image
    )
}