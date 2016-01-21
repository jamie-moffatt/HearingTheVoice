//
//  ILQuestion.m
//  Inner Life
//
//  Created by Matthew Bates on 12/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILQuestion.h"

@implementation ILQuestion

- (NSString*)description
{
    return [NSString stringWithFormat:@"(%ld,%ld)", (long)_questionID, (long)_number];
}

+ (ILQuestionType) typeFromString: (NSString*)string
{
    if ([string isEqualToString:@"RADIO"])
    {
        return RADIO;
    }
    else if ([string isEqualToString:@"YESNO"])
    {
        return YESNO;
    }
    else if ([string isEqualToString:@"NUMSCALE"])
    {
        return NUMSCALE;
    }
    else
    {
        return RADIO;
    }
}

@end
