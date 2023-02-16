package com.nigeleke.flac.codecs.metadata

import scodec._
import scodec.bits._
import scodec.codecs._

case class CuesheetTrack(trackOffset: Long,
                         trackNumber: Int,
                         isrc: String,
                         isAudio: Boolean,
                         isPreEmphasis: Boolean,
                         trackIndex: Vector[CuesheetTrackIndex])

object CuesheetTrack {

  //  <64>                  Track offset in samples, relative to the beginning of the FLAC audio stream. It is the offset to the first index point of the track. (Note how this differs from CD-DA, where the track's offset in the TOC is that of the track's INDEX 01 even if there is an INDEX 00.) For CD-DA, the offset must be evenly divisible by 588 samples (588 samples = 44100 samples/sec * 1/75th of a sec).
  //  <8>                   Track number. A track number of 0 is not allowed to avoid conflicting with the CD-DA spec, which reserves this for the lead-in. For CD-DA the number must be 1-99, or 170 for the lead-out; for non-CD-DA, the track number must for 255 for the lead-out. It is not required but encouraged to start with track 1 and increase sequentially. Track numbers must be unique within a CUESHEET.
  //  <12*8>                Track ISRC. This is a 12-digit alphanumeric code; see here and here. A value of 12 ASCII NUL characters may be used to denote absence of an ISRC.
  //  <1>                   The track type: 0 for audio, 1 for non-audio. This corresponds to the CD-DA Q-channel control bit 3.
  //  <1>                   The pre-emphasis flag: 0 for no pre-emphasis, 1 for pre-emphasis. This corresponds to the CD-DA Q-channel control bit 5; see here.
  //  <6+13*8>              Reserved. All bits must be set to zero.
  //  <8>                   The number of track index points. There must be at least one index in every track in a CUESHEET except for the lead-out track, which must have zero. For CD-DA, this number may be no more than 100.
  //  CUESHEET_TRACK_INDEX+ For all tracks except the lead-out track, one or more track index points.
  //
  val codec : Codec[CuesheetTrack] = {

    ("trackOffset" | long(64)) ::
      ("trackNumber" | uint8) ::
      ("isrc" | fixedSizeBytes(12, ascii)) ::
      ("isAudio" | bool) ::
      ("isPreEmphasis" | bool) ::
      ("reserved" | constant(BitVector(6+13*8, 0))) ::
      ("trackIndex" | vectorOfN(uint8, CuesheetTrackIndex.codec))

  }.as[CuesheetTrack]


}